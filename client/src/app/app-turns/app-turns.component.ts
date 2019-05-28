import {Component, OnInit, TemplateRef} from '@angular/core';
import {AuthService} from '../app-auth/services/auth.service';
import {ActivatedRoute, Router} from '@angular/router';
import {NzModalRef, NzModalService, NzNotificationService} from 'ng-zorro-antd';

@Component({
  selector: 'app-app-turns',
  templateUrl: './app-turns.component.html',
  styleUrls: ['./app-turns.component.css']
})
export class AppTurnsComponent implements OnInit {

  private currentUser: any;
  private currentTurns: any;
  confirmModal: NzModalRef;
  private turns: any;
  private onModal: boolean | false;
  private date = new Date();

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
    this.currentTurns = this.currentUser.turns;
    console.log(this.currentUser);
    console.log(this.currentTurns);
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

  getStops(turn) {
    return turn.line.name;
  }

  planTurn(date, modalTpl: TemplateRef<{}>) {
    this.createModal(date, modalTpl, this.getTurns(date));
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

  createModal(date, modalTpl: TemplateRef<{}>, turns) {
    this.turns = turns;
    this.date = date;
    this.confirmModal = this.modal.create({
      nzTitle: 'Update your turns',
      nzContent: modalTpl,
      nzStyle: {top: '20px'},
      nzClosable: false,
      nzFooter: []
    });
    this.onModal = true;
  }

  getColor(turn) {
    if (turn.direction === 'GOING') {
      return 'lime';
    } else {
      return 'rgba(17,169,201,0.83)';
    }
  }

  turnAdd(turn) {
    console.log(turn);
    turn.status = null;
    this.currentTurns.push(turn);
    this.currentUser.turns = this.currentTurns;
    localStorage.setItem('currentUser', JSON.stringify(this.currentUser));
    console.log(this.currentTurns);
    this.modal.closeAll();
    this.onModal = false;
  }

  turnChange(turn) {
    this.currentTurns = this.currentTurns.filter(d => d.id !== turn.id);
    console.log(turn);
    this.currentTurns.push(turn);
    this.currentUser.turns = this.currentTurns;
    localStorage.setItem('currentUser', JSON.stringify(this.currentUser));
    this.modal.closeAll();
    this.onModal = false;
  }

  turnDelete(id) {
    this.currentTurns = this.currentTurns.filter(d => d.id !== id);
    this.currentUser.turns = this.currentTurns;
    localStorage.setItem('currentUser', JSON.stringify(this.currentUser));
    this.modal.closeAll();
    this.onModal = false;
  }
}
