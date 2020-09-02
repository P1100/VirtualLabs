import {Component, EventEmitter, Input, OnDestroy, OnInit, Output, ViewChild} from '@angular/core';
import {Student} from '../../../models/student.model';
import {MatTableDataSource} from '@angular/material/table';
import {MatSort} from '@angular/material/sort';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {AlertsService} from '../../../services/alerts.service';
import {MatDialog, MatDialogRef, MatDialogState} from '@angular/material/dialog';
import {CourseService} from '../../../services/course.service';
import {SelectionModel} from '@angular/cdk/collections';
import {TeamProposeComponent} from '../../../dialogs/team-propose/team-propose.component';

@Component({
  selector: 'app-teams',
  templateUrl: './teams.component.html',
  styleUrls: ['./teams.component.css'],
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({height: '0px', minHeight: '0'})),
      state('expanded', style({height: '*'})),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)'))
    ])
  ]
})
export class TeamsComponent implements OnInit, OnDestroy {
  dataSource = new MatTableDataSource<Student>();
  displayedColumns: string[] = ['select', 'id', 'firstName', 'lastName', 'email'];
  private enrolledSelectedForProposal: Student[];
  dialogRef: MatDialogRef<any>;
  selection = new SelectionModel<Student>(true, []);

  @ViewChild(MatSort, {static: true}) sort: MatSort;

  @Output()
  forceUploadData = new EventEmitter<any>();

  @Input()
  set enrolledWithoutTeams(array: Student[]) {
    this.dataSource.data = [...array];
  }
  get enrolledWithoutTeams(): Student[] {
    return this.dataSource.data;
  }

  constructor(private alertsService: AlertsService, private courseService: CourseService, public dialog: MatDialog,) {
  }
  ngOnInit() {
    this.dataSource.sort = this.sort;
  }

  openProposeTeamDialog() {
    if (this.dialogRef?.getState() == MatDialogState.OPEN) {
      throw new Error('Error: Dialog stil open while opening a new one');
    }
    if (this.selection.selected.length >= 4) {
      throw new Error('Error: Selected more than 4 students');
    }
    if (this.selection.selected.length == 0) {
      this.alertsService.setAlert('warning', 'No students selected!');
      return;
    }
    this.dialogRef = this.dialog.open(TeamProposeComponent, {
      maxWidth: '400px', autoFocus: true, hasBackdrop: true, disableClose: true, closeOnNavigation: true,
      data: {studentsProposal: this.selection.selected}
    });
    this.dialogRef.afterClosed().subscribe((res: string) => {
        this.dialogRef = null;
        if (res != undefined) {
          this.forceUploadData.emit(null);
        }
      }, () => this.alertsService.setAlert('danger', 'Team Proposal dialog error')
    );
  }
  /** Whether the number of selected elements matches the total number of rows. */
  checkboxIsAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }
  /** Selects all rows if they are not all selected; otherwise clear selection. */
  checkboxMasterToggle() {
    this.checkboxIsAllSelected() ?
      this.selection.clear() :
      this.dataSource.data.forEach(row => this.selection.select(row));
  }
  checkboxChangeSelection(row: Student) {
    this.selection.toggle(row);
  }
  ngOnDestroy(): void {
    this.dialogRef?.close();
  }
  checkboxAreFourOrMoreSelected(row: Student) {
    if (!this.selection.isSelected(row) && this.selection.selected.length >= 4) {
      return true;
    }
    return false;
  }
}