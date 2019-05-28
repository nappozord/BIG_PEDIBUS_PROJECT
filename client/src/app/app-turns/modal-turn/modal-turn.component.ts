import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {first} from 'rxjs/operators';
import {AuthService} from '../../app-auth/services/auth.service';
import {NzNotificationService} from 'ng-zorro-antd';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-modal-turn',
  templateUrl: './modal-turn.component.html',
  styleUrls: ['./modal-turn.component.css']
})
export class ModalTurnComponent implements OnInit {

  @Input() date;
  @Input() turns: any;
  @Output() turnGotChanges = new EventEmitter<any>();
  @Output() turnGotNew = new EventEmitter<any>();
  @Output() turnGotDeleted = new EventEmitter<any>();

  private allLines: any[];
  private response: any;
  private edit_from = null;
  private edit_to = null;
  private direction;
  private stopLine_start;
  private stopLine_arrival;
  private line;
  private edited_going: number[] = [];
  private edited_return: number[] = [];

  constructor(
    private authService: AuthService,
    private notification: NzNotificationService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit() {

    this.notification = this.authService.setNotificationConfig();

    this.authService.getallLinesInfo()
      .pipe(first())
      .subscribe(
        data => {
          this.setLines(data);
          console.log(this.allLines);
        },
        error => {
          this.notification.error(
            'NApp',
            error || 'Sorry! Something went wrong. Please try again!'
          );
        });
  }

  setLines(res) {
    this.response = JSON.parse(JSON.stringify(res));
    this.allLines = this.response.lines;
    console.log(this.allLines);
  }

  getFrom(line: any, num): string {
    if (num === 0) {
      if (this.turns.filter(d => d.direction === 'GOING' && d.line.name === line.name).length > 0) {
        return this.turns.filter(d => d.direction === 'GOING' && d.line.name === line.name)[0].stopLine_start.stop.name;
      } else {
        return line.stopLines_going[0].stop.name;
      }
    } else {
      if (this.turns.filter(d => d.direction === 'RETURN' && d.line.name === line.name).length > 0) {
        return this.turns.filter(d => d.direction === 'RETURN' && d.line.name === line.name)[0].stopLine_arrival.stop.name;
      } else {
        return line.stopLines_return[line.stopLines_return.length - 1].stop.name;
      }
    }
  }

  getDest(line: any, num): string {
    if (num === 1) {
      if (this.turns.filter(d => d.direction === 'RETURN' && d.line.name === line.name).length > 0) {
        return this.turns.filter(d => d.direction === 'RETURN' && d.line.name === line.name)[0].stopLine_start.stop.name;
      } else {
        return line.stopLines_return[0].stop.name;
      }
    } else {
      if (this.turns.filter(d => d.direction === 'GOING' && d.line.name === line.name).length > 0) {
        return this.turns.filter(d => d.direction === 'GOING' && d.line.name === line.name)[0].stopLine_arrival.stop.name;
      } else {
        return line.stopLines_going[line.stopLines_going.length - 1].stop.name;
      }
    }
  }

  checkNew(line, num) {
    if (num === 0) {
      if (this.turns.filter(d => d.direction === 'GOING' && d.line.name === line.name).length > 0) {
        return false;
      }
      return true;
    } else {
      if (this.turns.filter(d => d.direction === 'RETURN' && d.line.name === line.name).length > 0) {
        return false;
      }
    }
    return true;
  }

  checkEdit(line: any, num, GorR) {
    if (GorR === 0 && this.edited_going !== undefined && this.edited_going.filter(d => d === line.id).length > 0) {
      return false;
    } else if (GorR === 1 && this.edited_return !== undefined && this.edited_return.filter(d => d === line.id).length > 0) {
      return false;
    }
    if (num === 0) {
      if (this.edit_from === line.id) {
        return false;
      }
      return true;
    } else {
      if (this.edit_to === line.id) {
        return false;
      }
      return true;
    }
  }

  submitValue_g(line, name, num) {
    if (num === 0) {
      for (let i = 0; i < this.allLines.length; i++) {
        if (this.allLines[i].id === line.id && this.allLines[i].stopLines_going.filter(d => d.stop.name === name).length > 0) {
          this.allLines[i].stopLines_going[0] = this.allLines[i].stopLines_going.filter(d => d.stop.name === name)[0];
        }
      }
      this.edit_from = null;
    } else {
      for (let i = 0; i < this.allLines.length; i++) {
        if (this.allLines[i].id === line.id && this.allLines[i].stopLines_going.filter(d => d.stop.name === name).length > 0) {
          this.allLines[i].stopLines_going[this.allLines[i].stopLines_going.length - 1] = this.allLines[i].stopLines_going
            .filter(d => d.stop.name === name)[0];
        }
      }
      this.edit_to = null;
    }
    console.log(this.allLines);
  }

  submitValue_r(line, name, num) {
    if (num === 0) {
      for (let i = 0; i < this.allLines.length; i++) {
        if (this.allLines[i].id === line.id && this.allLines[i].stopLines_return.filter(d => d.stop.name === name).length > 0) {
          this.allLines[i].stopLines_return[0] = this.allLines[i].stopLines_return.filter(d => d.stop.name === name)[0];
        }
      }
      this.edit_from = null;
    } else {
      for (let i = 0; i < this.allLines.length; i++) {
        if (this.allLines[i].id === line.id && this.allLines[i].stopLines_return.filter(d => d.stop.name === name).length > 0) {
          this.allLines[i].stopLines_return[this.allLines[i].stopLines_return.length - 1] = this.allLines[i].stopLines_return
            .filter(d => d.stop.name === name)[0];
        }
      }
      this.edit_to = null;
    }
    console.log(this.allLines);
  }

  checkOnTime(data, num) {
    const date = new Date();
    date.setHours(0, 0, 0, 0);
    this.date.setHours(0, 0, 0, 0);
    console.log(date);
    console.log(this.date);
    // CHANGEX
    if (date.getTime() === this.date.getTime()) {
      const date_ = new Date().getHours() + ':' + new Date().getMinutes();
      if (num === 0) {
        if (date_ > data.stopLines_going[0].hour) {
          return false;
        } else {
          return true;
        }
      } else {
        if (date_ > data.stopLines_return[0].hour) {
          return false;
        } else {
          return true;
        }
      }
    }
    console.log('HELLO');
    // CHANGEX
    if (date.getTime() > this.date.getTime()) {
      return false;
    }
    return true;
  }

  newTurn(line, num) {
    console.log(line);

    if (!this.checkOnTime(line, num)) {
      return;
    }

    if (num === 0) {
      this.stopLine_start = line.stopLines_going[0];
      this.stopLine_arrival = line.stopLines_going[line.stopLines_going.length - 1];
      this.direction = 'GOING';
    } else {
      this.stopLine_arrival = line.stopLines_return[line.stopLines_return.length - 1];
      this.stopLine_start = line.stopLines_return[0];
      this.direction = 'RETURN';
    }
    const options = {
      line: line.name,
      date: this.date,
      stopLine_start: this.stopLine_start,
      stopLine_arrival: this.stopLine_arrival,
      direction: this.direction,
      name: this.authService.getCurrentUserInfo().email,
      id: null
    };
    console.log(options);

    this.line = line;

    if (!options.id) {
      this.authService.turnConfirm(options)
        .pipe(first())
        .subscribe(
          data => {
            this.notification.success(
              'NApp',
              'Your turn has been added'
            );
            this.makeNewTurn(data, line);
          },
          error => {
            this.notification.error(
              'NApp',
              error || 'Something went wrong'
            );
          });
    }
  }

  makeNewTurn(res, line) {
    const turn = {
      line: line,
      date: this.date,
      stopLine_start: this.stopLine_start,
      stopLine_arrival: this.stopLine_arrival,
      direction: this.direction,
      id: res.id
    };
    console.log(turn);
    this.turnGotNew.emit(turn);
  }

  deleteTurn(line, num) {
    if (!this.checkOnTime(line, num)) {
      return;
    }

    let id;
    console.log(line);
    if (num === 0) {
      id = this.turns.filter(d => d.direction === 'GOING' && d.line.name === line.name)[0].id;
    } else {
      id = this.turns.filter(d => d.direction === 'RETURN' && d.line.name === line.name)[0].id;
    }
    console.log(id);
    this.authService.turnDelete(id)
      .pipe(first())
      .subscribe(
        data => {
          this.notification.success(
            'NApp',
            'Your turn has been deleted'
          );
          this.deleteTurnCallback(id);
        },
        error => {
          this.notification.error(
            'NApp',
            error || 'Something went wrong'
          );
        });
  }

  deleteTurnCallback(id) {
    this.turnGotDeleted.emit(id);
  }

  changeTurn(line, num) {
    if (!this.checkOnTime(line, num)) {
      return;
    }

    console.log(line);
    let id;

    if (num === 0) {
      this.stopLine_start = line.stopLines_going[0];
      this.stopLine_arrival = line.stopLines_going[line.stopLines_going.length - 1];
      this.direction = 'GOING';
      id = this.turns.filter(d => d.direction === 'GOING' && d.line.name === line.name)[0].id;
    } else {
      this.stopLine_arrival = line.stopLines_return[line.stopLines_return.length - 1];
      this.stopLine_start = line.stopLines_return[0];
      this.direction = 'RETURN';
      id = this.turns.filter(d => d.direction === 'RETURN' && d.line.name === line.name)[0].id;
    }

    const options = {
      line: line.name,
      date: this.date,
      stopLine_start: this.stopLine_start,
      stopLine_arrival: this.stopLine_arrival,
      direction: this.direction,
      status: null,
      name: this.authService.getCurrentUserInfo().email,
      id: id
    };
    console.log(options);

    this.line = line;
      this.authService.turnChange(options)
        .pipe(first())
        .subscribe(
          data => {
            this.notification.success(
              'NApp',
              'Your turn has been changed'
            );
            this.makeTurnChanges(data, id, line);
          },
          error => {
            this.notification.error(
              'NApp',
              error || 'Something went wrong'
            );
          });
  }

  makeTurnChanges(data, id, line) {
    const turn = {
      line: line,
      date: this.date,
      stopLine_start: this.stopLine_start,
      stopLine_arrival: this.stopLine_arrival,
      direction: this.direction,
      status: null,
      id: id
    };
    this.turnGotChanges.emit(turn);
  }
}
