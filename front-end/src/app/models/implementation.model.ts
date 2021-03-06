import {Student} from './student.model';
import {Image} from './image.model';

export class Implementation {
  constructor(
    public id: number,
    public status: string,   // status -->  {NULL, READ, SUBMITTED, REVIEWED, DEFINITIVE}
    public permanent: boolean,
    public grade: number,
    public readStatus: Date,
    public definitiveStatus: Date,
    public lastStatus: Date,
    public currentCorrection?: string,
    public student?: Student,
    public imageSubmissions?: Image[]) {
  }
}
