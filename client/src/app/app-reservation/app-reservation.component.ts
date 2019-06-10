import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormControl, FormGroup, ValidationErrors, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {AuthService} from '../app-auth/services/auth.service';
import {NzNotificationService} from 'ng-zorro-antd';
import {first} from 'rxjs/operators';
import {Observable, Observer} from 'rxjs';

@Component({
  selector: 'app-app-reservation',
  templateUrl: './app-reservation.component.html',
  styleUrls: ['./app-reservation.component.css']
})
export class AppReservationComponent implements OnInit {

  validateForm: FormGroup;
  counter = [0, 1, 2];
  private currentUser: any;
  private check = 0;
  private currentLine: any;
  private stopsList_going: any[] = [];
  private stopsList_return: any[] = [];
  private date: Date;
  private checked: boolean;
  private allLines;
  private defaultStopGoing;
  private defaultStopReturn;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private authService: AuthService,
    private notification: NzNotificationService
  ) {
  }

  ngOnInit() {

    this.notification = this.authService.setNotificationConfig();

    this.allLines = JSON.parse(localStorage.getItem('lines'));

    this.currentUser = this.authService.getCurrentUserInfo();

    this.checked = false;

    this.validateForm = this.formBuilder.group({
      name: ['', [Validators.required], [this.checkChildren]],
      line: ['', [Validators.required], [this.checkLines]]
    });

    console.log('check is: ' + this.check);
  }

  onValueChange(value: Date): void {
    console.log('Current value: ' + value);
    this.date = value;
  }

  onPanelChange(change: { date: Date; mode: string }): void {
    console.log(`Current value: ${change.date}`);
    console.log(`Current mode: ${change.mode}`);
  }

  checkChildren = (control: FormControl) =>
    new Observable((observer: Observer<ValidationErrors | null>) => {
      setTimeout(() => {
        for (let i = 0; i < this.currentUser.children.length; i++) {
          if (control.value === this.currentUser.children[i].childName) {
            observer.next(null);
            break;
          } else {
            observer.next({error: true, duplicated: true});
          }
        }
        observer.complete();
      }, 500);
    })

  checkLines = (control: FormControl) =>
    new Observable((observer: Observer<ValidationErrors | null>) => {
      setTimeout(() => {
        for (let i = 0; i < this.allLines.length; i++) {
          if (control.value === this.allLines[i]) {
            observer.next(null);
            break;
          } else {
            observer.next({error: true, duplicated: true});
          }
        }
        observer.complete();
      }, 500);
    })

  submitFormGoing($event: any, value: any) {

    console.log(this.checked);

    this.check++;

    console.log('check is: ' + this.check);

    this.authService.getLineInfo(this.validateForm.get('line').value.toString())
      .pipe(first())
          .subscribe(
            data => {
              this.setLine(data);
            },
            error => {
              this.notification.error(
                'PBus',
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
        id: i,
        stop: this.currentLine.stopLines_going[i].stop,
        hour: this.currentLine.stopLines_going[i].hour
      });
    }

    for (let i = 0; i < this.currentLine.stopLines_return.length; i++) {
      this.stopsList_return.push({
        id: i,
        stop: this.currentLine.stopLines_return[i].stop,
        hour: this.currentLine.stopLines_return[i].hour
      });
    }

    console.log(this.stopsList_going);
  }

  sendConfirm(data: any, direction: string) {
    this.check++;
    console.log(this.date.toString());
    const reservation = {
      line: this.currentLine.name,
      date: this.date.toString(),
      stop: data.stop.id,
      child: this.validateForm.get('name').value.toString(),
      direction: direction
    };
    console.log(reservation);

    this.authService.reservationConfirm(reservation)
      .pipe(first())
      .subscribe(
        asd => {
          this.notification.success(
            'PBus',
            'Reservation OK'
          );
          if (this.checked === true) {
            console.log('HELLO!!!!!');
            this.checkAndSendReturn(data.stop.id, data);
          }
          if (this.check === 3) {
            this.router.navigate(['/']);
          }
        },
          error => {
          this.notification.error(
          'PBus',
          error || 'Something went wrong!'
      );
      this.router.navigate(['/']);
    });
  }

  checkAndSendReturn(id, data: any) {
    for (let i = 0; i < this.stopsList_return.length; i++) {
      if (this.stopsList_return[i].stop.id === id) {
        this.checked = false;
        this.sendConfirm(data, 'RETURN');
        return;
      }
    }
    this.notification.error(
      'PBus',
      'The stop is not available for the return'
    );

  }

  changeCheck() {
    this.checked = !this.checked;
  }

}
