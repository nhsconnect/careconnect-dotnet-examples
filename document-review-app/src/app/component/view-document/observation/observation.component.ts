import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-observation',
  templateUrl: './observation.component.html',
  styleUrls: ['./observation.component.css']
})
export class ObservationComponent implements OnInit {

  @Input() observations : fhir.Observation[];
  constructor() { }

  ngOnInit() {
  }



  getValue(observation : fhir.Observation) : string {
    //console.log("getValue called" + observation.valueQuantity.value);
    if (observation == undefined) return "";

    if (observation.valueQuantity != undefined ) {
      //console.log(observation.valueQuantity.value);
      return observation.valueQuantity.value.toPrecision(4) + " " + observation.valueQuantity.unit;
    }

    if (observation.component == undefined || observation.component.length < 2)
      return "";
    // Only coded for blood pressures at present
    if (observation.component[0].valueQuantity == undefined )
      return "";
    if (observation.component[1].valueQuantity == undefined )
      return "";

    return observation.component[0].valueQuantity.value + "/"+ observation.component[1].valueQuantity.value + " " + observation.component[1].valueQuantity.unit;

  }

}
