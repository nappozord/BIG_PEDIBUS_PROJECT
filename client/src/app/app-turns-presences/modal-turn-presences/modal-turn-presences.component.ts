import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {AuthService} from '../../app-auth/services/auth.service';
import {NzModalRef, NzNotificationService} from 'ng-zorro-antd';
import {first} from 'rxjs/operators';

@Component({
  selector: 'app-modal-turn-presences',
  templateUrl: './modal-turn-presences.component.html',
  styleUrls: ['./modal-turn-presences.component.css']
})
export class ModalTurnPresencesComponent implements OnInit {

  @Input() turns;
  @Input() date;
  @Input() currentUser;
  @Input() closed: boolean;
  @Output() turnGotChanges = new EventEmitter<any>();
  @Output() closeModal = new EventEmitter<any>();

  private edit: number | null;
  private users: any[] = [];
  private selectedLine;
  private value: number[] = [0, 6];
  private conf: any[] = [];
  private canc: any[] = [];

  constructor(
    private authService: AuthService,
    private notification: NzNotificationService
  ) { }

  ngOnInit() {
    this.notification = this.authService.setNotificationConfig();

    console.log(this.turns);
    console.log(this.currentUser.lines);
  }

  getFrom(line: any): string {
    return line.stopLines_going[0].stop.name;
  }

  getDest(line: any): string {
    return line.stopLines_return[0].stop.name;
  }

  getStopTurn(turn): string {
    return 'FROM: ' + turn.stopLine_start.stop.name + ' - TO: ' + turn.stopLine_arrival.stop.name;
  }

  selectUsers(line, num) {
    this.users = [];
    console.log(line);
    if (num === 0) {
      this.selectedLine = line.stopLines_going;
      console.log(this.selectedLine);
      for ( let i = 0; i < this.turns.length; i++ ) {
        if (this.turns[i].line === line.name && this.turns[i].direction === 'GOING') {
          this.users.push(this.turns[i]);
        }
      }
    } else {
      this.selectedLine = line.stopLines_return;
      console.log(this.selectedLine);
      for ( let i = 0; i < this.turns.length; i++ ) {
        if (this.turns[i].line === line.name && this.turns[i].direction === 'RETURN') {
          this.users.push(this.turns[i]);
        }
      }
    }

    console.log(this.users);
    this.edit = line.id;
  }

  getSlide(turn) {
    const range: number[] = [];
    for (let i = 0; i < this.selectedLine.length; i++) {
      if (this.selectedLine[i].stop.name === turn.stopLine_start.stop.name
        || this.selectedLine[i].stop.name === turn.stopLine_arrival.stop.name) {
        range.push(i);
      }
    }
    if (this.value.toString() !== range.toString()) {
      this.value[0] = range[0];
      this.value[1] = range[1];
    }
    return true;
  }

  checkOnTime(data) {
    console.log(data);
    const date = new Date();
    date.setHours(0, 0, 0, 0);
    if (date.toString() === this.date.toString()) {
      const date_ = new Date().getHours() + ':' + new Date().getMinutes();
      if (data.direction === 'GOING') {
        if (date_ > data.stopLine_start.hour) {
          return false;
        } else {
          return true;
        }
      } else {
        if (date_ > data.stopLine_arrival.hour) {
          return false;
        } else {
          return true;
        }
      }
    }
    if (date.toString() < this.date.toString()) {
      return false;
    }
    return true;
  }

  setConfirmed(turn) {
    if (!this.checkOnTime(turn)) {
      return;
    }
    console.log(turn);
    for (let i = 0; i < this.users.length; i++) {
      if (this.users[i].id === turn.id) {
        if (this.users[i].status !== 'CONFIRMED') {
          this.users[i].status = 'CONFIRMED';
          this.conf.push(turn.id);
          this.canc = this.canc.filter(d => d !== turn.id);
        } else {
          this.users[i].status = null;
          this.canc.push(turn.id);
          this.conf = this.conf.filter(d => d !== turn.id);
        }
      }
    }
    console.log(this.conf);
    console.log(this.canc);
  }

  changeStatus() {
    if (this.closed !== false) {
      this.closed = false;
      this.authService.turnDecide(this.conf, this.canc)
        .pipe(first())
        .subscribe(
          data => {
            this.notification.success(
              'NApp',
              'Turns confirmed'
            );
            console.log(data);
            this.turnGotChanges.emit();
          },
          error => {
            this.notification.error(
              'NApp',
              error || 'There was some error'
            );
            this.closeModal.emit();
          }
        );
    }
    return true;
  }

  getAvatarColor(name) {
    return this.authService.getAvatarColor(name);
  }



}
