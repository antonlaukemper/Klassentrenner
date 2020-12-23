import { BrowserModule } from '@angular/platform-browser';
import { APP_INITIALIZER, NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CreateComponent } from './create/create.component';
import { AppConfigService } from './app-config.service';
import { HttpClientModule } from '@angular/common/http';
import { TeacherViewComponent } from './teacher-view/teacher-view.component';
import { NotFoundComponent } from './not-found/not-found.component';
import { SurveyOpenComponent } from './teacher-view/survey-open/survey-open.component';
import { IndexComponent } from './index/index.component';
import { QRCodeModule } from 'angularx-qrcode';
import { StudentViewComponent } from './student-view/student-view.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TeacherCleanUpComponent } from './teacher-view/teacher-clean-up/teacher-clean-up.component';
import { WaitingForResultComponent } from './teacher-view/waiting-for-result/waiting-for-result.component';
import { ResultsComponent } from './teacher-view/results/results.component';

@NgModule({
  declarations: [
    AppComponent,
    CreateComponent,
    TeacherViewComponent,
    NotFoundComponent,
    SurveyOpenComponent,
    IndexComponent,
    StudentViewComponent,
    TeacherCleanUpComponent,
    WaitingForResultComponent,
    ResultsComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    QRCodeModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [
    HttpClientModule,
    {
      provide: APP_INITIALIZER,
      multi: true,
      deps: [AppConfigService],
      useFactory: (appConfigService: AppConfigService) => {
        return () => {
          //Make sure to return a promise!
          return appConfigService.loadAppConfig();
        };
      }
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
