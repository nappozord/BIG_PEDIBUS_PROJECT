import { Component, OnInit } from '@angular/core';
import {AuthService} from '../app-auth/services/auth.service';
import {first} from 'rxjs/operators';
import {NzNotificationService} from 'ng-zorro-antd';
import {Router} from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  private currentUser;
  private currentTurn;
  private currentStopLines;
  private currentReservation_going;
  private currentReservation_return;
  private currentReservation;
  private currentChild_going;
  private currentChild_return;
  private date = new Date();
  private allChildren;
  private GorR: boolean;

  constructor(
    private authService: AuthService,
    private router: Router,
    private notification: NzNotificationService
  ) { }

  ngOnInit() {
    this.notification = this.authService.setNotificationConfig();

    this.GorR = true;

    if (new Date().getHours() > 12) {
      this.GorR = false;
    }

    this.currentUser = this.authService.getCurrentUserInfo();

    this.authService.currentUser.subscribe(user => {
      this.currentUser = user;
    });

    this.authService.setAllChildren();

    this.authService.allChildren.subscribe(children => {
      this.allChildren = children;
    });

    console.log(this.allChildren);

    this.getStopLines();

  }

  selectGoingOrReturn() {
    this.GorR = !this.GorR;
    this.getStopLines();
  }

  getStopLines() {
    this.date.setHours(0, 0, 0, 0);
    // CHANGEX
    this.currentTurn = this.currentUser.turns.filter(d => this.authService.stringToDate(d.date.toString()) === this.date.getTime());
    console.log(this.currentTurn);
    if (this.currentTurn !== null) {
      if (this.GorR === true) {
        if (this.currentTurn.filter(d => d.direction === 'GOING').length > 0) {
          this.currentTurn = this.currentTurn.filter(d => d.direction === 'GOING')[0];
          this.currentStopLines = this.currentTurn.line.stopLines_going;
          console.log(this.currentStopLines);
        } else {
          this.currentTurn = null;
          this.currentStopLines = [];
        }
      } else {
        if (this.currentTurn.filter(d => d.direction === 'RETURN').length > 0) {
          this.currentTurn = this.currentTurn.filter(d => d.direction === 'RETURN')[0];
          this.currentStopLines = this.currentTurn.line.stopLines_return;
          console.log(this.currentStopLines);
        } else {
          this.currentTurn = null;
          this.currentStopLines = [];
        }
      }
      console.log(this.currentTurn);
      if (this.currentTurn !== null) {
        this.getReservations(this.currentTurn.line.name);
      }
    }
  }

  getReservations(line) {

    const options = {
      line: line,
      date: this.date
    };

    this.authService.getLineReservation(options)
      .pipe(first())
      .subscribe(
        (data: any) => {
          console.log(data);
          this.currentReservation_going = data.reservation_going;
          this.currentReservation_return = data.reservation_return;
          this.currentChild_going = data.child_going;
          this.currentChild_return = data.child_return;
          if (this.GorR) {
            this.currentReservation = this.currentReservation_going;
          } else {
            this.currentReservation = this.currentReservation_return;
          }
        },
        error => {
          this.notification.error(
            'NApp',
            error || 'Something went wrong'
          );
        }
      );
  }

  getChildReservation(reservation, stopLine) {
    if (reservation.stopLine.stop.name === stopLine.stop.name) {
      return true;
    }
    return false;
  }

  getChildDefault(stopLine) {
    const names = [];
    if (this.GorR) {
      for (const child of this.currentChild_going.filter(d => d.defaultGoing.stop.name === stopLine.stop.name)) {
        names.push(child.childName);
      }
    } else {
      for (const child of this.currentChild_return.filter(d => d.defaultReturn.stop.name === stopLine.stop.name)) {
        names.push(child.childName);
      }
    }
    return names;
  }

  setMarked(reservation) {

    const reservation_ = {
      line: this.currentTurn.line.name,
      date: reservation.date,
      id_res: reservation.id,
      stop: reservation.stopLine.stop.id,
      child: reservation.child,
      direction: reservation.stopLine.direction,
      status: null
    };

    if (reservation.status === null) {
      reservation_.status = 'TAKEN';
    }

    console.log(reservation_);

    this.authService.reservationUpdate(reservation_)
      .pipe(first())
      .subscribe(
        data => {
          this.notification.success(
            'NApp',
            'Kid taken!'
          );
          this.currentReservation.filter(d => d.id === reservation_.id_res)[0].status = reservation_.status;
        },
        error => {
          this.notification.error(
            'NApp',
            error || 'Something went wrong'
          );
        }
      );

  }

  setReservation(user, stopLine) {
    console.log(stopLine);

    const reservation = {
      line: this.currentTurn.line.name,
      date: this.date,
      stop: stopLine.stop.id,
      child: user,
      direction: stopLine.direction,
      id: stopLine.id,
      status: 'TAKEN'
    };

    console.log(reservation);

    this.authService.reservationConfirm(reservation)
      .pipe(first())
      .subscribe(
        (data: any) => {
          this.notification.success(
            'NApp',
            'Kid taken!'
          );
          this.setConfirmation(reservation, data.id, stopLine);
        },
        error => {
          this.notification.error(
            'NApp',
            error || 'Something went wrong'
          );
        }
      );
  }

  setConfirmation(reservation, id, stopLine) {
    const reservation_ = {
      child: reservation.child,
      date: reservation.date,
      id: id,
      status: reservation.status,
      stopLine: stopLine
    };

    if (this.GorR) {
      this.currentReservation_going.push(reservation_);
      this.currentChild_going = this.currentChild_going.filter(d => d.childName !== reservation.child);
    } else {
      this.currentReservation_return.push(reservation_);
      this.currentChild_return = this.currentChild_return.filter(d => d.childName !== reservation.child);
    }

    console.log(this.currentChild_going);
    console.log(this.currentReservation_going);
  }

  getAvatarColor(name) {
    return this.authService.getAvatarColor(name);
  }

  goToChild(child: any) {
    this.router.navigate(['/children', child.childName]);
  }

  hasNotDefaultYet(child) {
    if (child.lineDefault === null) {
      return true;
    }
    return false;
  }

  isChildTaken(child) {
    if (new Date().getHours() < 12) {
      // CHANGEX
      if (child.reservations.filter(d => this.authService.stringToDate(d.date.toString()) === this.date.getTime() && d.stopLine.direction === 'GOING').length > 0) {
        // CHANGEX
        if (child.reservations.filter(d => this.authService.stringToDate(d.date.toString()) === this.date.getTime()
          && d.stopLine.direction === 'GOING')[0].status === 'TAKEN') {
          return true;
        } else {
          return false;
        }
      } else {
        return false;
      }
    } else {
      // CHANGEX
      if (child.reservations.filter(d => this.authService.stringToDate(d.date.toString()) === this.date.getTime() && d.stopLine.direction === 'RETURN').length > 0) {
        // CHANGEX
        if (child.reservations.filter(d => this.authService.stringToDate(d.date.toString()) === this.date.getTime()
          && d.stopLine.direction === 'RETURN')[0].status === 'TAKEN') {
          return true;
        } else {
          return false;
        }
      } else {
        return false;
      }
    }
  }

  getWaitingInStop(child) {
    if (new Date().getHours() < 12) {
      // CHANGEX
      if (child.reservations.filter(d => this.authService.stringToDate(d.date.toString()) === this.date.getTime() && d.stopLine.direction === 'GOING').length > 0) {
        // CHANGEX
        return child.reservations.filter(d => this.authService.stringToDate(d.date.toString()) === this.date.getTime()
          && d.stopLine.direction === 'GOING')[0].stopLine.stop.name;
      } else {
        return child.child.defaultGoing.stop.name;
      }
    } else {
      // CHANGEX
      if (child.reservations.filter(d => this.authService.stringToDate(d.date.toString()) === this.date.getTime() && d.stopLine.direction === 'RETURN').length > 0) {
        // CHANGEX
        return child.reservations.filter(d => this.authService.stringToDate(d.date.toString()) === this.date.getTime()
          && d.stopLine.direction === 'RETURN')[0].stopLine.stop.name;
      } else {
        return child.child.defaultReturn.stop.name;
      }
    }
  }

}
