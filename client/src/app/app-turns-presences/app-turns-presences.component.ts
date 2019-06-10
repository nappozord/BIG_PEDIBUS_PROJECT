import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {AuthService} from '../app-auth/services/auth.service';
import {NzModalRef, NzModalService, NzNotificationService} from 'ng-zorro-antd';
import {first} from 'rxjs/operators';
import {ModalTurnPresencesComponent} from './modal-turn-presences/modal-turn-presences.component';

@Component({
  selector: 'app-app-turns-presences',
  templateUrl: './app-turns-presences.component.html',
  styleUrls: ['./app-turns-presences.component.css']
})
export class AppTurnsPresencesComponent implements OnInit {

  @ViewChild(ModalTurnPresencesComponent) modal_turns_presences;

  private currentUser: any;
  private currentTurns: any[] = [];
  confirmModal: NzModalRef;
  private turns: any;
  private onModal: boolean | false;
  private date = new Date();
  private closed: boolean;

  constructor(
    private router: Router,
    private authService: AuthService,
    private route: ActivatedRoute,
    private notification: NzNotificationService,
    private modal: NzModalService
  ) { }

  ngOnInit() {
    this.notification = this.authService.setNotificationConfig();

    this.currentUser = this.authService.getCurrentUserInfo();

    for (let i = 0; i < this.currentUser.lines.length; i++) {
      this.authService.getTurnsByLine(this.currentUser.lines[i].id)
        .pipe(first())
        .subscribe(
          data => {
            this.setTurns(data, this.currentUser.lines[i].name);
            console.log(this.currentTurns);
          },
          error => {
            this.notification.error(
              'PBus',
              error || 'Something went wrong'
            );
          });
    }
  }

  setTurns(data, line) {
    const turns = JSON.parse(JSON.stringify(data)).turns;
    const users = JSON.parse(JSON.stringify(data)).users;
    for (let i = 0; i < turns.length; i++) {
      turns[i].line = line;
      this.currentTurns.push({
        ...turns[i],
        user: users[i]
      });
    }
  }

  getCellDate(date, date2) {
    const now = new Date();
    now.setHours(0, 0, 0, 0);
    if (this.authService.stringToDate(date) < now.getTime()) {
      return false;
    }
    // CHANGEX
    if (this.authService.stringToDate(date) === this.authService.stringToDate(date2.date)) {
      return true;
    }
    return false;
  }

  getNames(turn) {
    return turn.line;
  }

  getColor(turn) {
    if (turn.direction === 'GOING') {
      return 'lime';
    } else {
      return 'rgba(17,169,201,0.83)';
    }
  }

  getTurns(date) {
    const turns = [];
    for ( let i = 0; i < this.currentTurns.length; i++) {
      // CHANGEX
      if (this.authService.stringToDate(date) === this.authService.stringToDate(this.currentTurns[i].date)) {
        turns.push(this.currentTurns[i]);
      }
    }
    console.log(turns);
    return turns;
  }

  setConfirmedTurns(date, modalTpl: TemplateRef<{}>) {
    this.createModal(date, modalTpl, this.getTurns(date));
  }

  createModal(date, modalTpl: TemplateRef<{}>, turns) {
    this.closed = false;
    this.turns = turns;
    this.date = date;
    this.confirmModal = this.modal.create({
      nzTitle: 'Update your turns',
      nzContent: modalTpl,
      nzStyle: {top: '20px'},
      nzClosable: false,
      nzFooter: [
        {
          label: 'Confirm',
          type: 'primary',
          onClick: () => this.closed = true
        }
      ]
    });
    this.onModal = true;
  }

  turnChange() {
    console.log(this.currentTurns);
    for (let i = 0; i < this.currentUser.turns.length; i++) {
      this.currentUser.turns[i].status = this.currentTurns
        .filter(d => d.user === this.currentUser.email && d.id === this.currentUser.turns[i].id)[0].status;
    }
    console.log(this.currentUser);
    localStorage.setItem('currentUser', JSON.stringify(this.currentUser));
    this.modal.closeAll();
    this.onModal = false;
  }

  closeModal() {
    this.modal.closeAll();
    this.onModal = false;
  }

}
