import {HttpHeaders} from '@angular/common/http';
import {HateoasModel} from './models/hateoas.model';
import {throwError} from 'rxjs';
import {environment} from '../environments/environment';

/* Shared settings, constants and functions */
export class AppSettings {
  // Back end URL
  public static baseUrl = 'http://localhost:8080';
  public static devtest: boolean = environment.dev; // to show logs, tests, and other components

  // HTTP Settings (services)
  public static RETRIES = 1;
  public static JSON_HTTP_OPTIONS: object = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json;charset=utf-8',
      Accept: 'application/json', // , text/plain, */*
    })
    , responseType: 'json'
  };

// Dynamic build of r1 tabs menu
  static tabsStudent = [
    {path: 'teams', label: 'Teams'},
    {path: 'vms', label: 'VMs'},
    {path: 'assignments', label: 'Assignments'}
  ];
  static tabsProfessor = [
    {path: 'students', label: 'Students'},
    {path: 'vms', label: 'VMs'},
    {path: 'assignments', label: 'Assignments'}
  ];
}

/* HATEOAS api DESIGN: all http returned objects are converted to array (empty, full, or singleton), to uniform handling. Alternatives: @Projection */
export function removeHATEOAS(container: HateoasModel): any[] {
  if (container == null) {
    return [];
  }
  delete container?._links;
  delete container?.links;
  let innerList: any = container?._embedded;
  if (innerList?.courseDTOList != null) {
    innerList = innerList.courseDTOList;
  }
  if (innerList?.studentDTOList != null) {
    innerList = innerList.studentDTOList;
  }
  if (innerList?.professorDTOList != null) {
    innerList = innerList.professorDTOList;
  }
  if (innerList?.imageDTOList != null) {
    innerList = innerList.imageDTOList;
  }
  if (innerList?.teamDTOList != null) {
    innerList = innerList.teamDTOList;
  }
  if (innerList?.vmDTOList != null) {
    innerList = innerList.vmDTOList;
  }
  if (innerList?.implementationDTOList != null) {
    innerList = innerList.implementationDTOList;
  }
  innerList = innerList?.map((element: any) => {
    delete element?._links;
    delete element?.links;
    return element;
  });
  delete innerList?._links;
  delete innerList?.links; // only '_links' should show up
  const result = innerList != null ? innerList : container;
  if (Array.isArray(result)) {
    return result;
  } else if (typeof result == 'object') {
    if (Object.keys(result)?.length == 0) {
      return [];
    } else {
      return [result];
    }
  } else {
    console.warn('HTTP HateOASModel: not an object, not an array.');
    return [];
  }
}

// Uniform all data received from services, converting any (mostly objects) to arrays
export function getSafeDeepCopyToArray(ss: any): any[] {
  return JSON.parse(JSON.stringify(ss));
}
export function formatErrors(error: any) {
  let responseErrorString = error?.error?.message;
  if (AppSettings.devtest) {
    responseErrorString = responseErrorString + ` [${error?.error?.status} ${error?.error?.error}]`;
    console.error('FOMART: ', error, responseErrorString);
  }
  return throwError(responseErrorString);
}
