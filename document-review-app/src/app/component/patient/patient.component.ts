import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ResourceDialogComponent} from "../resource-dialog/resource-dialog.component";
import {MatDialog, MatDialogConfig, MatDialogRef} from "@angular/material";

@Component({
  selector: 'app-patient',
  templateUrl: './patient.component.html',
  styleUrls: ['./patient.component.css']
})
export class PatientComponent implements OnInit {

  @Input() patients : fhir.Patient[];

  @Input() showDetail : boolean = false;

  @Output() patient = new EventEmitter<any>();


  constructor( public dialog: MatDialog) { }

  ngOnInit() {
  }
  select(resource) {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    dialogConfig.data = {
      id: 1,
      resource: resource
    };
    let resourceDialog : MatDialogRef<ResourceDialogComponent> = this.dialog.open( ResourceDialogComponent, dialogConfig);
  }
}
