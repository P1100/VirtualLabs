import {CourseService} from '../../../services/course.service';
import {Router} from '@angular/router';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {Student} from '../../../models/student.model';
import {MatDialog, MatDialogRef, MatDialogState} from '@angular/material/dialog';
import {MatTableDataSource} from '@angular/material/table';
import {Component, EventEmitter, Input, OnDestroy, Output} from '@angular/core';
import {VmCreateComponent} from '../../../dialogs/vm-create/vm-create.component';
import {AlertsService} from '../../../services/alerts.service';
import {Team} from '../../../models/team.model';
import {Vm} from '../../../models/vm.model';

@Component({
  selector: 'app-vms-prof',
  templateUrl: './vms-prof.component.html',
  styleUrls: ['./vms-prof.component.css'],
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({height: '0px', minHeight: '0'})),
      state('expanded', style({height: '*'})),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ],
})
export class VmsProfComponent implements OnDestroy {
  displayedColumnsTable1: string[] = ['select', 'id', 'firstName', 'lastName', 'email'];
  columnsToDisplayProposals: string[] = ['nav', 'proposer', 'name', 'createdDate', 'confirm', 'reject']; //, 'accept', 'reject'
  columnsToLoadFromTeam: string[] = ['name', 'active', 'disabled', 'createdDate'];
  columnsToDisplayStudent: string[] = ['id', 'firstName', 'lastName'];
  expandedElement: Student | null;

  dataSourceEnrolledNoTeams = new MatTableDataSource<Student>();
  dataSourceTeams = new MatTableDataSource<Team>();
  dialogRef: MatDialogRef<any>;
  @Input()
  idStringLoggedStudent;
  indexLoggedUser: any[] = [];
  @Input()
  activeTeam: Team = null;
  innerCourseId: string;
  @Input()
  set courseId(id: string) {
    this.innerCourseId = id;
  }
  get courseId() {
    return this.innerCourseId;
  }
  @Output()
  forceRefreshData = new EventEmitter<any>();
  @Input()
  hideAllGUItillActiveTeamIsChecked: boolean;

  constructor(private alertsService: AlertsService, private courseService: CourseService, public dialog: MatDialog, private router: Router) {
  }

  openCreateVmDialog() {
    if (this.dialogRef?.getState() == MatDialogState.OPEN) {
      throw new Error('Error: Dialog stil open while opening a new one');
    }
    if (this.activeTeam?.id == null) {
      this.alertsService.setAlert('danger', 'Error: no active team for this course');
      return;
    }
    const proposalData: Vm = new Vm(0, 0, 0, false, +this.idStringLoggedStudent, +this.activeTeam.id);
    this.dialogRef = this.dialog.open(VmCreateComponent, {
      maxWidth: '400px', autoFocus: true, hasBackdrop: true, disableClose: true, closeOnNavigation: true, data: proposalData
    });
    this.dialogRef.afterClosed().subscribe((res: string) => {
        this.dialogRef = null;
        if (res != undefined) {
          setTimeout(() => {
            this.forceRefreshData.emit(null);
          }, 150);
        }
      }, () => this.alertsService.setAlert('danger', 'VM creation dialog error')
    );
  }
  ngOnDestroy(): void {
    this.dialogRef?.close();
  }
}

