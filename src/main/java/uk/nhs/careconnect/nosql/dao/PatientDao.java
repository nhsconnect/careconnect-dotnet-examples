package uk.nhs.careconnect.nosql.dao;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import org.hl7.fhir.dstu3.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.careconnect.nosql.dao.transform.PatientEntityToFHIRPatient;

import uk.nhs.careconnect.nosql.entities.IdentifierEntity;
import uk.nhs.careconnect.nosql.entities.Name;
import uk.nhs.careconnect.nosql.entities.PatientEntity;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import uk.nhs.careconnect.nosql.entities.Telecom;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Repository
public class PatientDao implements IPatient {

    private static final Logger log = LoggerFactory.getLogger(PatientDao.class);

    @Autowired
    private MongoOperations mongo;

    @Autowired
    private PatientEntityToFHIRPatient patientEntityToFHIRPatient;

    @Override
    public Patient read(FhirContext ctx, IdType theId) {
        ObjectId objectId = new ObjectId(theId.getIdPart());
        Query qry = Query.query(Criteria.where("_id").is(objectId));
        System.out.println(qry.toString());
        PatientEntity patientEntity = mongo.findOne(qry, PatientEntity.class);
        if (patientEntity == null) return null;
        return patientEntityToFHIRPatient.transform(patientEntity);
    }

    @Override
    public PatientEntity createEntity(FhirContext ctx, Patient patient) {

        // TODO This is a basic patient find and would need extending for a real implementtion.

        for (Identifier identifier : patient.getIdentifier()) {
            identifier.setValue(identifier.getValue().replace(" ",""));
            Query qry = Query.query(Criteria.where("identifiers.system").is(identifier.getSystem()).and("identifiers.value").is(identifier.getValue()));

            PatientEntity patientE = mongo.findOne(qry, PatientEntity.class);
            // Patient found, quit and do not add new record.
            if (patientE!=null) return patientE;
        }

        PatientEntity patientEntity = new PatientEntity();

        for (Identifier identifier : patient.getIdentifier()) {
            identifier.getValue().replaceAll(" ","");
            IdentifierEntity identifierE = new IdentifierEntity(identifier);

            patientEntity.getIdentifiers().add(identifierE);
        }
        for (HumanName name : patient.getName()) {
            Name nameE = new Name();
            nameE.setFamilyName(name.getFamily());
            nameE.setGivenName(name.getGivenAsSingleString());
            if (name.hasPrefix()) {
                nameE.setPrefix(name.getPrefix().get(0).getValue());
            }
            if (name.hasUse()) {
                nameE.setNameUse(name.getUse());
            }
            patientEntity.getNames().add(nameE);
        }
        if (patient.hasBirthDate()) {
            patientEntity.setDateOfBirth(patient.getBirthDate());
        }
        if (patient.hasGender()) {
            patientEntity.setGender(patient.getGender());
        }
        for (ContactPoint contactPoint : patient.getTelecom()) {
            Telecom telecom = new Telecom();
            telecom.setValue(contactPoint.getValue());
            if (contactPoint.hasSystem()) {
                telecom.setSystem(contactPoint.getSystem());
            }
            if (contactPoint.hasUse()) telecom.setTelecomUse(contactPoint.getUse());

            patientEntity.getTelecoms().add(telecom);
        }
        for (Address address : patient.getAddress()) {
            uk.nhs.careconnect.nosql.entities.Address addressEntity = new uk.nhs.careconnect.nosql.entities.Address();

            for (StringType line : address.getLine()) {
                addressEntity.getLines().add(line.toString());
            }

            if (address.hasCity()) {
                addressEntity.setCity(address.getCity());
            }
            if (address.hasPostalCode()) {
                addressEntity.setPostcode(address.getPostalCode());
            }
            if (address.hasDistrict()) {
                addressEntity.setCounty(address.getDistrict());
            }
            if (address.hasUse()) {
                addressEntity.setUse(address.getUse());
            }

            patientEntity.getAddresses().add(addressEntity);
        }
        mongo.save(patientEntity);

       return patientEntity;
    }

    @Override
    public List<Resource> search(FhirContext ctx, StringParam postCode, DateRangeParam birthDate, TokenParam email, StringParam familyName, TokenParam gender, StringParam givenName, TokenParam identifier, StringParam name, TokenParam phone) {
        List<Resource> resources = new ArrayList<>();

        Criteria criteria = null;

        // http://127.0.0.1:8181/STU3/Patient?identifier=https://fhir.leedsth.nhs.uk/Id/pas-number|LOCAL1098
        if (identifier != null) {
            if (criteria ==null) {
                criteria = Criteria.where("identifiers.system").is(identifier.getSystem()).and("identifiers.value").is(identifier.getValue());
            } else {
                criteria.and("identifiers.system").is(identifier.getSystem()).and("identifiers.value").is(identifier.getValue());
            }
        }
        if (familyName!=null) {
            if (criteria ==null) {
                criteria = Criteria.where("names.familyName").regex(familyName.getValue(), "i");
            } else {
                criteria.and("names.familyName").regex(familyName.getValue(), "i");
            }
        }
        if (givenName!=null) {
            if (criteria ==null) {
                criteria = Criteria.where("names.givenName").regex(givenName.getValue(), "i");
            } else {
                criteria.and("names.givenName").regex(givenName.getValue(), "i");
            }
        }
        if (name!=null) {

            String regexName = name.getValue() ; //.toLowerCase()+".*"; // use options = i for regex
            if (criteria ==null) {
                criteria = new Criteria().orOperator(Criteria.where("names.familyName").regex(regexName, "i"),Criteria.where("names.givenName").regex(regexName, "i"));
            } else {
                criteria.orOperator(Criteria.where("names.familyName").regex(regexName, "i"),Criteria.where("names.givenName").regex(regexName, "i"));
            }
        }

        if (postCode!=null) {
            if (criteria ==null) {
                criteria = Criteria.where("addresses.postcode").is(postCode.getValue());
            } else {
                criteria.and("addresses.postcode").is(postCode.getValue());
            }
        }

        if (birthDate!=null) {
            if (criteria ==null) {
                criteria = Criteria.where("dateOfBirth").gte(birthDate.getLowerBound().getValue()).lte(birthDate.getUpperBound().getValue());
            } else {
                criteria.and("dateOfBirth").gte(birthDate.getLowerBound().getValue()).lte(birthDate.getUpperBound().getValue());
            }
        }

        if (phone!=null) {
            if (criteria ==null) {
                criteria = Criteria.where("telecoms.value").is(phone.getValue()).and("telecoms.system").is(ContactPoint.ContactPointSystem.PHONE);
            } else {
                criteria.and("telecoms.value").is(phone.getValue()).and("telecoms.system").is(ContactPoint.ContactPointSystem.PHONE);
            }
        }

        if (email!=null) {
            if (criteria ==null) {
                criteria = Criteria.where("telecoms.value").is(email.getValue()).and("telecoms.system").is(ContactPoint.ContactPointSystem.EMAIL);
            } else {
                criteria.and("telecoms.value").is(email.getValue()).and("telecoms.system").is(ContactPoint.ContactPointSystem.EMAIL);
            }
        }

        if (gender!=null) {
            if (criteria ==null) {
                criteria = Criteria.where("gender").is(gender.getValue().toUpperCase());
            } else {
                criteria.and("gender").is(gender.getValue().toUpperCase());
            }
        }

        if (criteria != null) {
            Query qry = Query.query(criteria);

            log.debug("About to call Mongo DB for a patient=[{}]", qry.toString());

            List<PatientEntity> patientResults = mongo.find(qry, PatientEntity.class);

            log.debug("Found [{}] result(s)", patientResults.size());

            for (PatientEntity patientEntity : patientResults) {
                resources.add(patientEntityToFHIRPatient.transform(patientEntity));
            }
        }

        return resources;
    }
}
