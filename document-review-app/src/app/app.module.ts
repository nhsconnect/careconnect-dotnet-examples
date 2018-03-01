import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';


import { AppComponent } from './app.component';
import { AppRoutingModule } from './/app-routing.module';
import { LoadDocumentComponent } from './component/load-document/load-document.component';
import { NavComponent } from './component/nav/nav.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';

import { LoadDocumentFileComponent } from './component/load-document-file/load-document-file.component';
import {FileUploadModule} from "ng2-file-upload";
import { ViewDocumentComponent } from './component/view-document/view-document.component';
import {FhirService} from "./service/fhir.service";
import {HttpClientModule} from "@angular/common/http";
import { ViewDocumentSectionComponent } from './component/view-document-section/view-document-section.component';




@NgModule({
  declarations: [
    AppComponent,
    LoadDocumentComponent,
    NavComponent,
    LoadDocumentFileComponent,
    ViewDocumentComponent,
    ViewDocumentSectionComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FileUploadModule,
    HttpClientModule,
    NgbModule.forRoot()

  ],
  providers: [
    FhirService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
