import {MatToolbarModule} from '@angular/material/toolbar';
import {MatFormFieldModule} from '@angular/material/form-field';
import {HomeComponent} from './r0-topheader-leftsidebar/home.component';
import {MatPaginatorModule} from '@angular/material/paginator';
import {LoginComponent} from './dialogs/login/login.component';
import {NgModule} from '@angular/core';
import {MatInputModule} from '@angular/material/input';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatMenuModule} from '@angular/material/menu';
import {AppRoutingModule} from './app-routing.module';
import {MatGridListModule} from '@angular/material/grid-list';
import {MatSortModule} from '@angular/material/sort';
import {MatTableModule} from '@angular/material/table';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatCardModule} from '@angular/material/card';
import {StudentsComponent} from './r2-inner-tab/teacher/students/students.component';
import {MatTabsModule} from '@angular/material/tabs';
import {MatTooltipModule} from '@angular/material/tooltip';
import {PageNotFoundComponent} from './r0-topheader-leftsidebar/page-not-found.component';
import {MatButtonModule} from '@angular/material/button';
import {TeamsComponent} from './r2-inner-tab/teams/teams.component';
import {LayoutModule} from '@angular/cdk/layout';
import {VmsContComponent} from './r2-inner-tab/vms/vms-cont.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {MatSelectModule} from '@angular/material/select';
import {MatIconModule} from '@angular/material/icon';
import {MatListModule} from '@angular/material/list';
import {MatSidenavModule} from '@angular/material/sidenav';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {AssignmentsContComponent} from './r2-inner-tab/assignments/assignments-cont.component';
import {BrowserModule} from '@angular/platform-browser';
import {TestingComponent} from './r2-inner-tab/testing/testing.component';
import {EmptyComponent} from './r1-tabs-menu/empty.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {StudentsContComponent} from './r2-inner-tab/teacher/students/students-cont.component';
import {AppComponent} from './app.component';
import {MAT_DIALOG_DEFAULT_OPTIONS, MatDialogModule} from '@angular/material/dialog';
import {RegisterComponent} from './dialogs/register/register.component';
import {TabsNavComponent} from './r1-tabs-menu/tabs-nav.component';
import {CourseEditComponent} from './dialogs/course-edit/course-edit.component';
import {MatRadioModule} from '@angular/material/radio';
import {CourseDeleteComponent} from './dialogs/course-delete/course-delete.component';
import {CourseAddComponent} from './dialogs/course-add/course-add.component';
import {AuthInterceptor} from './services/auth.interceptor';

@NgModule({
  imports: [
    MatInputModule,
    BrowserModule,
    BrowserAnimationsModule,
    MatToolbarModule,
    MatSidenavModule,
    MatListModule,
    MatIconModule,
    MatTabsModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatCardModule,
    MatAutocompleteModule,
    MatFormFieldModule,
    MatCheckboxModule,
    FormsModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    // Removing these wont generate an error, but GUI wont display properly
    MatDialogModule,
    MatButtonModule,
    MatMenuModule,
    MatTooltipModule,
    // New unchecked
    MatSelectModule,
    MatExpansionModule,
    NgbModule, //ng bootstrap
    MatGridListModule,
    LayoutModule,
    MatRadioModule,
  ],
  declarations: [
    AppComponent,
    StudentsComponent,
    StudentsContComponent,
    PageNotFoundComponent,
    VmsContComponent,
    TeamsComponent,
    AssignmentsContComponent,
    HomeComponent,
    TabsNavComponent,
    LoginComponent,
    EmptyComponent,
    LoginComponent,
    RegisterComponent,
    TestingComponent,
    CourseEditComponent,
    CourseDeleteComponent,
    CourseAddComponent,
  ],
  providers: [HttpClientModule, {provide: MAT_DIALOG_DEFAULT_OPTIONS, useValue: {hasBackdrop: false}},
    {provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true}],
  bootstrap: [AppComponent]
})
export class AppModule {
}
