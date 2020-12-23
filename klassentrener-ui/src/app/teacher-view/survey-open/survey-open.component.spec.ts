import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { AppConfigService, MockAppConfigService } from 'src/app/app-config.service';
import { SchoolClassStatus } from 'src/app/models';
import { SchoolClassResolver } from 'src/app/_resolvers/school-class.resolver';
import { TeacherService } from 'src/app/_services/teacher.service';
import { ClassTeacher, EncTools, SchoolClass } from 'src/app/_tools/enc-tools.service';

import { SurveyOpenComponent } from './survey-open.component';

class MockTeacherService{
  nSignups(schoolClass: any, teacher: any){
    return of(42);
  }
}


describe('SurveyOpenComponent', () => {
  let component: SurveyOpenComponent;
  let fixture: ComponentFixture<SurveyOpenComponent>;
  
  let schoolClass: SchoolClass;
  let teacher: ClassTeacher;

  beforeAll( async () => {
    await EncTools.makeClass("test school", "test class", "test password").toPromise().then(
      ([sCls, teeach]: [SchoolClass,  ClassTeacher]) => {
        sCls.id = 23;
        sCls.status = SchoolClassStatus.open;
        schoolClass = sCls;
        teacher = teeach;
      }
    );
  });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [{provide: AppConfigService, useClass: MockAppConfigService},
        {provide: TeacherService, useClass: MockTeacherService}
      ],
      declarations: [ SurveyOpenComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SurveyOpenComponent);
    component = fixture.componentInstance;
    component.classTeacher = teacher;
    component.schoolClass = schoolClass;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
