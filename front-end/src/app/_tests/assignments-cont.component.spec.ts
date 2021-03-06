import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';

import {AssignmentsContComponent} from '../r2-inner-tab/assignments/assignments-cont.component';

describe('AssignmentsContComponent', () => {
  let component: AssignmentsContComponent;
  let fixture: ComponentFixture<AssignmentsContComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [AssignmentsContComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AssignmentsContComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
