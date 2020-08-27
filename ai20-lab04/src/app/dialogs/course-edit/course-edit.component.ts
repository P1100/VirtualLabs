import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {Course} from '../../models/course.model';
import {CourseService} from '../../services/course.service';

@Component({
  selector: 'app-course-edit',
  templateUrl: './course-edit.component.html',
  styleUrls: ['./course-edit.component.css']
})
export class CourseEditComponent {
  course = new Course('null', '', 0, 0, false, '');
  addressForm: any;
  private selectedFile: File;

  constructor(private courseService: CourseService,
              public dialogRef: MatDialogRef<CourseEditComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any) {
    const subscription = courseService.getCourse(data.id).subscribe(c => this.course = c[0]);
  }

  onCancelClick(): void {
    this.dialogRef.close('cancel');
  }
  onSubmit() {
    this.courseService.updateCourse(this.course).subscribe();
    this.dialogRef.close('refresh');
  }

  public onFileChanged(event) {
    this.course.enabled = !this.course.enabled;
    this.selectedFile = event.target.files[0];
    console.log(this.selectedFile);
    // To update bootstrap input text, missing JQuery
    this.course.vmModelPath = this.selectedFile.name;
  }
}