import {Component, ElementRef, OnDestroy, ViewChild} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {concatMap, toArray} from 'rxjs/operators';
import {StudentService} from '../../../services/student.service';
import {from, Observable, Subscription} from 'rxjs';
import {Student} from '../../../models/student.model';
import {getSafeDeepCopyToArray} from '../../../app-settings';
import {AlertsService} from '../../../services/alerts.service';
import {CourseService} from '../../../services/course.service';

@Component({
  selector: 'app-students-cont',
  template: `
    <app-students [enrolled]="enrolledStudents"
                  [students]="allStudents"
                  (enrolledEvent)="onStudentsToEnroll($event)"
                  (disenrolledEvent)="onStudentsToDisenroll($event)"
                  (uploadCsvEvent)="onCsvUpload($event)"
    ></app-students>
  `,
  styleUrls: []
})
export class StudentsContComponent implements OnDestroy {
  // updated after every route change, inside constructor
  allStudents: Student[] = [];
  enrolledStudents: Student[] = [];
  courseId = '0';
  // constructor subscriptions to paramMap
  subAllStudents: Subscription = null;
  subEnrolledStudentsCourse: Subscription = null;
  subRouteParam: Subscription = null;

  @ViewChild('labelFileCsv')
  labelFileName: ElementRef;

  // Routing change update (e.g. when changing course)
  constructor(private backendService: StudentService, private activatedRoute: ActivatedRoute, private alertsService: AlertsService
    , private courseService: CourseService) {
    this.subRouteParam = this.activatedRoute.paramMap.subscribe(() => {
        this.courseId = this.activatedRoute.parent.snapshot.paramMap.get('id');
        this.subEnrolledStudentsCourse = this.backendService.getEnrolledStudents(this.courseId)
          .subscribe((students: Student[]) => {
              this.enrolledStudents = Array.isArray(students) ? [...students] : [];
            }, error => this.alertsService.setAlert('danger', 'Couldn\'t get enrolled students! ' + error)
          );
        this.subAllStudents = this.backendService.getAllStudents()
          .subscribe((students: Student[]) => {
              this.allStudents = Array.isArray(students) ? [...students] : [];
            }, error => this.alertsService.setAlert('danger', 'Couldn\'t get students list! ' + error)
          );
      }
    );
  }
  ngOnDestroy(): void {
    this.subRouteParam.unsubscribe();
    this.subAllStudents.unsubscribe();
    this.subEnrolledStudentsCourse.unsubscribe();
  }

  onStudentsToEnroll(studentsToEnroll: Student[]) {
    if (studentsToEnroll === null || studentsToEnroll.length === 0) {
      this.alertsService.setAlert('danger', 'Couldn\'t enroll!');
      return;
    }
    const observable: Observable<Student[]> = from([...studentsToEnroll])
      .pipe(
        concatMap((student: Student) =>
          this.backendService.enroll(student, this.courseId) as Observable<any>
        ),
        toArray()  // so it waits for all inner observables to collect, after source complete
      ) as Observable<Student[]>;
    this.updateEnrolledAfterEnrollChange(observable, 'enroll');
  }
  onStudentsToDisenroll(studentsToDisenroll: Student[]) {
    if (studentsToDisenroll === null || studentsToDisenroll.length === 0) {
      this.alertsService.setAlert('danger', 'Couldn\'t disenroll!');
      return;
    }
    const observable: Observable<Student[]> = from(studentsToDisenroll).pipe(
      concatMap((student: Student) =>
        this.backendService.disenroll(student, this.courseId) as Observable<any>
      ),
      toArray()  // so it waits for all inner observables to collect
    ) as Observable<Student[]>;
    this.updateEnrolledAfterEnrollChange(observable, 'disenroll');
  }
  // Updating table data, basically
  private updateEnrolledAfterEnrollChange(o: Observable<Student[]>, message: string) {
    o.subscribe(() => {
      this.backendService.getEnrolledStudents(this.courseId).subscribe(
        (ss: Student[]) => {
          this.enrolledStudents = getSafeDeepCopyToArray(ss);
          this.alertsService.setAlert('success', `Student ${message}ed!`);
        }, error => this.alertsService.setAlert('danger', `Couldn\'t update enrolled students! ${error}`));
    }, error => this.alertsService.setAlert('danger', `Couldn\'t ${message}! ${error}`));
  }
  onCsvUpload(selectedFile: File) {
    const uploadCSVData = new FormData();
    uploadCSVData.append('file', selectedFile);
    this.courseService.enrollStudentsCSV(this.courseId, uploadCSVData)
      .subscribe(() => {
          this.backendService.getEnrolledStudents(this.courseId).subscribe(
            (ss: Student[]) => {
              this.enrolledStudents = getSafeDeepCopyToArray(ss);
              this.alertsService.setAlert('success', `CSV students enrolled!`);
            }, error => this.alertsService.setAlert('danger', `Couldn\'t update enrolled students! ${error}`));
        },
        error => this.alertsService.setAlert('danger', `Couldn\'t upload CSV! ${error}`)
      );
  }
}
