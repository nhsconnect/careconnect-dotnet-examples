package uk.nhs.careconnect.nosql.providers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.careconnect.nosql.dao.IPatient;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

@Component
public class PatientProvider implements IResourceProvider {

    @Autowired
    FhirContext ctx;

    @Autowired
    IPatient patientDao;

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Patient.class;
    }


    @Search
    public List<Resource> searchPatient(HttpServletRequest request,
                                        @OptionalParam(name = Patient.SP_ADDRESS_POSTALCODE) StringParam postCode,
                                        @OptionalParam(name = Patient.SP_BIRTHDATE) DateRangeParam birthDate,
                                        @OptionalParam(name = Patient.SP_EMAIL) TokenParam email,
                                        @OptionalParam(name = Patient.SP_FAMILY) StringParam familyName,
                                        @OptionalParam(name = Patient.SP_GENDER) TokenParam gender,
                                        @OptionalParam(name = Patient.SP_GIVEN) StringParam givenName,
                                        @OptionalParam(name = Patient.SP_IDENTIFIER) TokenParam identifier,
                                        @OptionalParam(name = Patient.SP_NAME) StringParam name,
                                        @OptionalParam(name = Patient.SP_PHONE) TokenParam phone
    ) {

        //List<Resource> results = patientDao.search(ctx,postCode, birthDate,email, familyName,gender,givenName,identifier,name, phone);
        //TODO: Two sample patients with ids, could have been inserted into DB, but hardcoded for demo
        List<Resource> results = asList(
                new Patient()
                        .setName(singletonList(new HumanName().setFamily("Smith").setPrefix(asList(new StringType("Mr"))).setGiven(asList(new StringType("John")))))
                        .setGender(AdministrativeGender.MALE)
                        .setBirthDate(new Date())
                        .setId("9446309239"),
                new Patient()
                        .setName(singletonList(new HumanName().setFamily("HALAOUJ").setPrefix(asList(new StringType("Mrs"))).setGiven(asList(new StringType("Topaz"), new StringType("Meadow")))))
                        .setGender(AdministrativeGender.FEMALE)
                        .setBirthDate(new Date())
                        .setId("9995000784")
        );

        return results;

    }

    /*
@Read
public Patient readPatient(HttpServletRequest request, @IdParam IdType internalId) {

    Patient patient = patientDao.read(ctx,internalId);

    return patient;
}
*/

}
