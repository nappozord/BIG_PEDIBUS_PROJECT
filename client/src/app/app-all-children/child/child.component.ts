import {Component, OnInit, TemplateRef} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {AuthService} from '../../app-auth/services/auth.service';
import {NzModalRef, NzModalService, NzNotificationService} from 'ng-zorro-antd';
import {first} from 'rxjs/operators';
import {ModalRefComponent} from './modal-ref/modal-ref.component';

@Component({
  selector: 'app-child',
  templateUrl: './child.component.html',
  styleUrls: ['./child.component.css'],
})
export class ChildComponent implements OnInit {

  private currentChild: any;
  private currentReservationHacks: any;
  private currentChildName;
  confirmModal: NzModalRef;
  private bool: boolean | false;
  private onModal: boolean | false;
  private id_;
  private date_ = new Date();
  private idStopLine;
  private id_res;
  private status_old;

  constructor(
    private router: Router,
    private authService: AuthService,
    private route: ActivatedRoute,
    private notification: NzNotificationService,
    private modal: NzModalService
  ) { }

  ngOnInit() {

    this.notification = this.authService.setNotificationConfig();

    this.route.params.subscribe(params => {
      this.currentChildName = params['child'];
    });

    this.authService.getChildInfo(this.currentChildName)
      .pipe(first())
      .subscribe(
        data => {
          this.setChild(data);
          console.log(this.currentChild);
        },
        error => {
          this.notification.error(
            'PBus',
            'The child does not exist'
          );
          this.router.navigate(['/']);
        });
  }

  setChild(res) {

    this.currentChild = JSON.parse(JSON.stringify(res)).child;

    this.currentReservationHacks = JSON.parse(JSON.stringify(res)).reservationHacks;

    console.log(this.currentReservationHacks);
  }

  getCellDate(date, date2) {
    const now = new Date();
    now.setHours(0, 0, 0, 0);
    if (this.authService.stringToDate(date) < now.getTime()) {
      return false;
    }
    // CHANGEX
    if (this.authService.stringToDate(date) === this.authService.stringToDate(date2.date)) {
      this.bool = true;
      return true;
    }
    return false;
  }

  isReservationDeleted(date2) {
    if (date2.status === 'deleted') {
      return false;
    }
    return true;
  }

  getStopsNotDefault(reservationHacks) {
    if (reservationHacks.stopLine.status !== 'deleted') {
      return reservationHacks.stopLine.stop.name;
    }
    return 'No reservation';
  }

  check(date, direction) {
    const now = new Date();
    now.setHours(0, 0, 0, 0);
    if (date.getTime() < now.getTime()) {
      return false;
    }

    if (this.authService.holidays(date)) {
      for ( let i = 0; i < this.currentReservationHacks.length; i++) {
        // CHANGEX
        if (this.authService.stringToDate(date) === this.authService.stringToDate(this.currentReservationHacks[i].date)
          && this.currentReservationHacks[i].stopLine.direction === direction) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  going_return(reservation) {
    if (reservation.stopLine.direction === 'GOING') {
      return true;
    }
    return false;
  }

  reservationUpdate(date_, id_, modalTpl: TemplateRef<{}>, idStopLine, id_res, status) {
    console.log();
    if (this.currentChild.lineDefault) {
      this.id_ = id_;
      this.date_ = date_;
      this.idStopLine = idStopLine;
      this.id_res = id_res;
      this.status_old = status;
      this.confirmModal = this.modal.create({
        nzTitle: 'Update your reservation',
        nzContent: modalTpl,
        nzStyle: {top: '20px'},
        nzClosable: false,
        nzFooter: [
          {
            label: ''
          }
        ]
      });
      this.onModal = true;
    } else {
      this.reservationDelete(date_, id_res);
    }
  }

  reservationAdd(reservation) {
    console.log(reservation);
    this.currentReservationHacks.push(reservation);
    console.log(this.currentReservationHacks);
    this.modal.closeAll();
    this.onModal = false;
  }

  reservationDelete(date, id_res) {
    console.log(id_res);
    this.authService.reservationDelete(id_res)
      .pipe(first())
      .subscribe(
        result => {
          this.notification.success(
            'PBus',
            'Your reservation has been deleted'
          );
          console.log(this.currentReservationHacks);
          this.currentReservationHacks = this.currentReservationHacks.filter( d => d.id !== id_res);
          console.log(this.currentReservationHacks);
        },
        error => {
          this.notification.error(
            'PBus',
            error || 'Something went wrong'
          );
        });
  }

  reservationChange(reservation) {
    console.log(reservation);
    for (let i = 0; i < this.currentReservationHacks.length; i++) {
      if (this.currentReservationHacks[i].id === reservation.id_res) {
        this.currentReservationHacks[i] = reservation;
      }
    }
    console.log(this.currentReservationHacks);
    this.modal.closeAll();
    this.onModal = false;
  }

}
