import { ComponentFixture, TestBed } from '@angular/core/testing';
import { StudentInEdit } from '../../teacher-clean-up.models';

import { StudentDetailComponent } from './student-detail.component';

describe('StudentDetailComponent', () => {
  let component: StudentDetailComponent;
  let fixture: ComponentFixture<StudentDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StudentDetailComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StudentDetailComponent);
    component = fixture.componentInstance;
    component.studentEntity = StudentInEdit.makeTeacherAdded("Tim K")
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
