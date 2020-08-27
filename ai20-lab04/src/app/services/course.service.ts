import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Observable} from 'rxjs';
import {Course} from '../models/course.model';
import {catchError, map, retry, tap} from 'rxjs/operators';
import {AppSettings, formatErrors, removeHATEOAS} from '../app-settings';
import {Student} from '../models/student.model';
import {HateoasModel} from '../models/hateoas.model';
import {Team} from '../models/team.model';

@Injectable({
  providedIn: 'root'
})
export class CourseService {
  private baseUrlAPI = environment.baseUrl + '/courses';

  constructor(private http: HttpClient) {
  }

  getCourses(): Observable<Course[]> {
    return this.http.get<HateoasModel>(`${this.baseUrlAPI}`, AppSettings.JSON_HTTP_OPTIONS)
      .pipe(
        map(object => removeHATEOAS(object)),
        retry(AppSettings.RETRIES), catchError(formatErrors),
        tap(res => console.log('--getCourses:', res))
      );
  }
  addCourse(course: Course) {
    return this.http.post(`${this.baseUrlAPI}`, JSON.stringify(course), AppSettings.JSON_HTTP_OPTIONS)
      .pipe(catchError(formatErrors));
  }
  updateCourse(course: Course) {
    return this.http.put(`${this.baseUrlAPI}`, JSON.stringify(course), AppSettings.JSON_HTTP_OPTIONS)
      .pipe(retry(AppSettings.RETRIES), catchError(formatErrors));
  }

  getCourse(courseId: string): Observable<Course[]> {
    return this.http.get<HateoasModel>(`${this.baseUrlAPI}/${courseId}`, AppSettings.JSON_HTTP_OPTIONS)
      .pipe(
        map(object => removeHATEOAS(object)),
        retry(AppSettings.RETRIES), catchError(formatErrors),
        tap(res => console.log('--getCourse:', res))
      );
  }
  deleteCourse(courseId: string) {
    return this.http.delete(`${this.baseUrlAPI}/${courseId}`, AppSettings.JSON_HTTP_OPTIONS)
      .pipe(retry(AppSettings.RETRIES), catchError(formatErrors));
  }

  enableCourse(courseId: string) {
    return this.http.put(`${this.baseUrlAPI}/${courseId}/enable`, null, AppSettings.JSON_HTTP_OPTIONS)
      .pipe(retry(AppSettings.RETRIES), catchError(formatErrors));
  }
  disableCourse(courseId: string) {
    return this.http.put(`${this.baseUrlAPI}/${courseId}/disable`, null, AppSettings.JSON_HTTP_OPTIONS)
      .pipe(retry(AppSettings.RETRIES), catchError(formatErrors));
  }

  getEnrolledStudents(courseId: string): Observable<Student[]> {
    return this.http.get<HateoasModel>(`${this.baseUrlAPI}/${courseId}/enrolled`, AppSettings.JSON_HTTP_OPTIONS)
      .pipe(
        map(object => removeHATEOAS(object)),
        retry(AppSettings.RETRIES), catchError(formatErrors),
        tap(res => console.log('--getEnrolledStudents:', res))
      );
  }

  disenrollStudent(studentId: number, courseId: string) {
    return this.http.put(`${this.baseUrlAPI}/${courseId}/disenroll/${studentId}`, null, AppSettings.JSON_HTTP_OPTIONS)
      .pipe(catchError(formatErrors));
  }
  enrollStudent(student: Student, courseId: string) {
    return this.http.post(`${this.baseUrlAPI}/${courseId}/enroll`, JSON.stringify(student), AppSettings.JSON_HTTP_OPTIONS)
      .pipe(catchError(formatErrors));
  }
  enrollStudents(studentsId: number[], courseId: string) {
    return this.http.post(`${this.baseUrlAPI}/${courseId}/enroll-all`, JSON.stringify(studentsId), AppSettings.JSON_HTTP_OPTIONS)
      .pipe(catchError(formatErrors));
  }
  enrollStudentsCSV(courseId: string, uploadCSVData: FormData): Observable<any> {
    return this.http.post(`${this.baseUrlAPI}/${courseId}/enroll-csv`, uploadCSVData)
      .pipe(catchError(formatErrors));
  }

  getTeamsForCourse(courseId: string): Observable<Team[]> {
    return this.http.get<HateoasModel>(`${this.baseUrlAPI}/${courseId}/teams`, AppSettings.JSON_HTTP_OPTIONS)
      .pipe(
        map(object => removeHATEOAS(object)),
        retry(AppSettings.RETRIES), catchError(formatErrors),
        tap(res => console.log('--getTeamsForCourse:', courseId, res))
      );
  }
  getStudentsInTeams(courseId: string): Observable<Student[]> {
    return this.http.get<HateoasModel>(`${this.baseUrlAPI}/${courseId}/students-in-teams`, AppSettings.JSON_HTTP_OPTIONS)
      .pipe(
        map(object => removeHATEOAS(object)),
        retry(AppSettings.RETRIES), catchError(formatErrors),
        tap(res => console.log('--getStudentsInTeams:', courseId, res))
      );
  }
  getAvailableStudents(courseId: string): Observable<Student[]> {
    return this.http.get<HateoasModel>(`${this.baseUrlAPI}/${courseId}/students-available`, AppSettings.JSON_HTTP_OPTIONS)
      .pipe(
        map(object => removeHATEOAS(object)),
        retry(AppSettings.RETRIES), catchError(formatErrors),
        tap(res => console.log('--getAvailableStudents:', courseId, res))
      );
  }
}