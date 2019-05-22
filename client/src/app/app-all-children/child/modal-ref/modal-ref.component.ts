import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {NzModalRef} from 'ng-zorro-antd';
import {ActivatedRoute, Router} from '@angular/router';
import {first} from 'rxjs/operators';
import {NzNotificationService} from 'ng-zorro-antd';
import {AuthService} from '../../../app-auth/services/auth.service';

@Component({
  selector: 'app-modal-ref',
  templateUrl: './modal-ref.component.html',
  styleUrls: ['./modal-ref.component.css']
})
export class ModalRefComponent implements OnInit {

  @Input() child: any;
  @Input() id: any;
  @Input() date: string;
  @Input() idStopLine: any;
  @Input() id_res: any;
  @Input() status_old: any;
  @Output() reservationGotChanges = new EventEmitter<any>();
  @Output() reservationGotNew = new EventEmitter<any>();

  private currentLine: any;
  private currentLineName: string;
  private currentChildName: string;
  private stopsList_going: any[] = [];
  private stopsList_return: any[] = [];
  private disabled: boolean;
  private stopReservation = [];
  private selectIndex;
  private direction;
  private stopLine;
  private status: string | null;

  constructor(
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    private notification: NzNotificationService
  ) { }

  ngOnInit() {

    this.notification = this.authService.setNotificationConfig();

    console.log(this.id);

    this.currentLineName = this.child.lineDefault.name;

    this.currentChildName = this.child.childName;

    if (this.id === 0) {
      this.disabled = true;
    } else {
      this.disabled = false;
    }

    this.selectIndex = this.id;

    console.log(this.currentChildName);

    this.authService.getLineInfo(this.currentLineName)
      .pipe(first())
      .subscribe(
        data => {
          this.setLine(data);
          console.log(this.currentLine);
        },
        error => {
          this.notification.error(
            'NApp',
            'The line does not exist'
          );
          this.router.navigate(['/']);
        });
  }

  setLine(res) {
    this.currentLine = JSON.parse(JSON.stringify(res)).line;

    console.log(this.currentLine);

    for (let i = 0; i < this.currentLine.stopLines_going.length; i++) {
      this.stopsList_going.push({
        id:  this.currentLine.stopLines_going[i].id,
        stop: this.currentLine.stopLines_going[i].stop,
        hour: this.currentLine.stopLines_going[i].hour,
        status: this.currentLine.stopLines_going[i].status
      });
    }

    console.log((this.idStopLine));

    console.log(this.stopsList_going);

    for (let i = 0; i < this.currentLine.stopLines_return.length; i++) {
      this.stopsList_return.push({
        id: this.currentLine.stopLines_return[i].id,
        stop: this.currentLine.stopLines_return[i].stop,
        hour: this.currentLine.stopLines_return[i].hour,
        status: this.currentLine.stopLines_return[i].status
      });
    }

    console.log(this.stopsList_return);
  }

  setStopReservation(data) {
    if (this.checkOnTime(data)) {
      this.stopReservation = data;
      this.disabled = !this.disabled;
      this.selectIndex = 1;

      this.submitReservation(data);
    }
  }

  checkOnTime(data) {
    const date = new Date();
    date.setHours(0, 0, 0, 0);
    if (date.toString() === this.date.toString()) {
      const date_ = new Date().getHours() + ':' + new Date().getMinutes();
      if (date_ > data.hour) {
        return false;
      } else {
        return true;
      }
    }
    return true;
  }

  checkIfDefault() {
    if (this.id === 0) {
      this.direction = 'GOING';
      if (this.child.defaultGoing.id === this.idStopLine
        && !this.id_res) {
        this.idStopLine = null;
      }
    } else {
      this.direction = 'RETURN';
      if (this.child.defaultReturn.id === this.idStopLine
        && !this.id_res) {
        this.idStopLine = null;
      }
    }
  }

  deleteStopReservation(data) {
    if (!this.checkOnTime(data)) {
      return;
    }

    this.status = 'deleted';
    this.stopLine = data;

    console.log(data);

    this.checkIfDefault();

    this.stopLine.direction = this.direction;
    this.stopLine.status = 'deleted';

    const options = {
      line: this.currentLineName,
      date: this.date,
      child: this.currentChildName,
      stop: this.stopLine.stop.id,
      id: this.idStopLine,
      direction: this.direction,
      status: 'deleted',
      id_res: this.id_res
    };

    console.log(options);

    this.submit(options);
  }

  submitReservation(data) {
    this.stopLine = data;
    console.log(this.child);
    console.log(data);
    console.log(this.id_res);

    this.checkIfDefault();

    console.log(this.idStopLine);

    this.stopLine.direction = this.direction;

    const reservation = {
      line: this.currentLine.name,
      date: this.date,
      stop: data.stop.id,
      child: this.currentChildName,
      direction: this.direction,
      id: this.idStopLine,
      id_res: this.id_res
    };
    console.log(reservation);

    this.submit(reservation);
  }

  submit(reservation) {
    if (this.idStopLine) {
      console.log(reservation);
      this.authService.reservationUpdate(reservation)
        .pipe(first())
        .subscribe(
          res => {
            this.notification.success(
              'NApp',
              'Your reservation has been updated'
            );
            this.updateOldReservation(res);
          },
          error => {
            this.notification.error(
              'NApp',
              error || 'Something went wrong'
            );
          });
    } else {
      console.log(reservation);
      this.authService.reservationConfirm(reservation)
        .pipe(first())
        .subscribe(
          res => {
            this.notification.success(
              'NApp',
              'Your new reservation has been made'
            );
            this.makeNewReservation(res);
          },
          error => {
            this.notification.error(
              'NApp',
              error || 'Something went wrong'
            );
          });
    }
  }

  isDisabled() {
    return this.disabled;
  }

  makeNewReservation(res) {
    const reservation = {
      id: res.id,
      date: this.date.toString(),
      stopLine: this.stopLine,
      status: this.status
    };
    console.log(reservation);
    this.reservationGotNew.emit(reservation);
  }

  updateOldReservation(res) {
    const reservation = {
      id: this.idStopLine,
      id_res: this.id_res,
      date: this.date.toString(),
      stopLine: this.stopLine,
      status: this.status
    };
    console.log(reservation);
    this.reservationGotChanges.emit(reservation);
  }

  checkIfDeleted(data) {
    if (data.id === this.idStopLine) {
      if (this.id_res && this.status_old === 'deleted') {
        return true;
      }
      return false;
    }
    return true;
  }

}
