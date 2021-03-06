import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';

import {VmsStudComponent} from '../r2-inner-tab/student/vms/vms-stud.component';

describe('VmsComponent', () => {
  let component: VmsStudComponent;
  let fixture: ComponentFixture<VmsStudComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [VmsStudComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VmsStudComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
