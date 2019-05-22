import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { shareReplay, tap, first } from 'rxjs/operators';

import { UserInfo } from './../models/user-info';

import { environment } from 'src/environments/environment';
import {Subject} from 'rxjs';
import Stomp from 'stompjs';
import SockJS from 'sockjs-client';
import {NzNotificationService} from 'ng-zorro-antd';

const API_BASE_URL = environment.API_BASE_URL;

const colors = [
  '#F44336', '#e91e63', '#9c27b0', '#673ab7',
  '#ff9800', '#ff5722', '#795548', '#607d8b',
  '#3f51b5', '#2196F3', '#00bcd4', '#009688',
  '#2196F3', '#32c787', '#00BCD4', '#ff5652',
  '#ffc107', '#ff85af', '#FF9800', '#39bbb0',
  '#4CAF50', '#ffeb3b', '#ffc107',
];

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  currentUser: Subject<any> = new Subject<any>();
  linesListObs: Subject<any> = new Subject<any>();
  updatesObs: Subject<any> = new Subject<any>();
  allChildren: Subject<any> = new Subject<any>();
  updates: any[] = [];
  updatesCountObs;
  private stompClient2;
  filteredOptions: string[] = [];
  filteredOptions_c: string[] = [];
  linesList: any[] = [];
  now = new Date();
  linesList_: any[] = [];

  constructor(
    private http: HttpClient,
    private notification: NzNotificationService
  ) {}

  emailConfirmationReSend(email: string) {
    return this.http.post(API_BASE_URL + '/emailResend', {'email': email});
  }

  pass_recovery(username: string, password: string) {
    localStorage.setItem('email', username);
    return this.http.post(API_BASE_URL + '/passwordRecovery', {'email': username, 'password': password});
  }

  recoveryConfirmation(token: string) {
    return this.http.get(API_BASE_URL + '/recoveryConfirm?token=' + token);
  }

  register(user: UserInfo) {
    return this.http.post(API_BASE_URL + '/auth/signup?token=' + localStorage.getItem('token'), user);
  }

  register_from_admin(user: any) {
    localStorage.setItem('email', user.email);
    console.log(user);
    // tslint:disable-next-line:max-line-length
    return this.http.post(API_BASE_URL + '/auth/signup_from_admin', {'email': user.email.toString(), 'role': user.role.toString(), 'line': user.line.toString()});
  }

  reservationConfirm(reservation: any) {
    console.log(reservation);
    return this.http.post(API_BASE_URL + '/auth/reservations/' + reservation.line + '/' + reservation.date,
      { 'stop_id': reservation.stop, 'name': reservation.child, 'direction': reservation.direction,
      'stopLine_id': reservation.id, 'status': reservation.status});
  }

  reservationUpdate(reservation: any) {
    return this.http.put(API_BASE_URL + '/auth/reservations/' + reservation.line + '/' + reservation.date + '/' + reservation.id_res,
      { 'stop_id': reservation.stop, 'name': reservation.child, 'direction': reservation.direction,
        'status': reservation.status});
  }

  turnConfirm(turn: any) {
    return this.http.post(API_BASE_URL + '/auth/turns/' + turn.line + '/' + turn.date, {'stopLine_start': turn.stopLine_start,
    'stopLine_arrival': turn.stopLine_arrival, 'name': turn.name, 'direction': turn.direction});
  }

  turnDecide(id_conf: any[], id_canc: any[]) {
    return this.http.put(API_BASE_URL + '/auth/turns/confirmTurn', {'id_conf': id_conf, 'id_canc': id_canc});
  }

  turnChange(turn: any) {
    return this.http.put(API_BASE_URL + '/auth/turns/' + turn.line + '/' + turn.date + '/' + turn.id,
      {'stopLine_start': turn.stopLine_start, 'stopLine_arrival': turn.stopLine_arrival,
        'name': turn.name, 'direction': turn.direction});
  }

  turnDelete(id) {
    return this.http.delete(API_BASE_URL + '/auth/turns/' + id);
  }

  getTurnsByLine(id: number) {
    console.log(id);
    return this.http.get(API_BASE_URL + '/auth/turns/' + id);
  }

  setDefault(options: any) {
    return this.http.post(API_BASE_URL + '/auth/child/' + options.name + '/' + options.line,
      {'stop_going': options.stop_going, 'stop_return': options.stop_return});
  }

  login(username: string, password: string) {
    return this.http.post<any>(API_BASE_URL + '/auth/login', {'email': username, 'password': password})
        // this is just the HTTP call,
        // we still need to handle the reception of the token
        .pipe(tap(res => this.setSession(res)), // handles the auth result
        shareReplay());
        // prevents the receiver of this Observable from accidentally triggering multiple POST requests due to multiple
        // subscriptions.
  }

  private setSession(authResult) {
    localStorage.setItem('accessToken', authResult.accessToken);
    this.getCurrentUser();
    this.webSocketConnect();
    this.getAllLines();
  }

  getAllLines() {
    return this.http.get(API_BASE_URL + '/auth/lines/justTheNames')
      .pipe(first())
      .subscribe(
        (data: any) => {
          localStorage.setItem('lines', JSON.stringify(data.lines));
        }
      );
  }

  saveChanges(user: any) {
    console.log(user);
    console.log(API_BASE_URL + '/user/' + user.email);
    return this.http.put(API_BASE_URL + '/user/' + user.email, user);
  }

  isLoggedIn(): boolean {
    if (!localStorage.getItem('accessToken')) {
      return false;
    }
    // let update = JSON.parse(localStorage.getItem('updates'));
    if (this.updates === null) {
      this.updates = [];
    }
    this.updatesObs.next(this.updates);
    return true;
  }

  getCurrentUser() {
    if (!localStorage.getItem('accessToken')) {
      return;
    }

    this.http.get(API_BASE_URL + '/user/me')
      .pipe(first(), shareReplay())
      .subscribe((data: any) => {
          const user = data.user;
          console.log(data);
          user.turns = data.turnHacks;
          data.user.messages.sort((a, b) => a.mess_id > b.mess_id);
          console.log(data.user.messages.sort((a, b) => a.mess_id - b.mess_id));
          this.currentUser.next(data.user);
          localStorage.setItem('currentUser', JSON.stringify(data.user));
          this.setLinesList(user.lines);
          this.updatesCountObs = this.calculateNotificationCount(data);
        },
        error => {
        });
  }

  logout(): any {
    localStorage.clear();
    this.currentUser.next(undefined);
    this.updatesObs.next(undefined);
    this.linesListObs.next(undefined);
    this.stompClient2.disconnect();
  }

  getCurrentUserInfo(): any {
    const user = JSON.parse(localStorage.getItem('currentUser'));
    this.currentUser.next(user);
    this.setLinesList(user.lines);
    return user;
  }

  getLineReservation(options) {
    return this.http.get(API_BASE_URL + '/auth/reservations/' + options.line + '/' + options.date);
  }

  getAllUsersInfo() {
    if (!localStorage.getItem('accessToken')) {
      return;
    }

    return this.http.get(API_BASE_URL + '/auth/getAllUsers');
  }

  getallLinesInfo() {
    return this.http.get(API_BASE_URL + '/auth/lines');
  }

  getAllChildrenInfo() {
    return this.http.get(API_BASE_URL + '/auth/children/' + this.getCurrentUserInfo().email);
  }

  getAllChildrenInfoAndReservations() {
    return this.http.get(API_BASE_URL + '/auth/childrenAndReservations/' + this.getCurrentUserInfo().email);
  }

  getAvatarColor(name) {
    name = name.substr(0, 6);

    let hash = 0;
    for (let i = 0; i < name.length; i++) {
      hash = 31 * hash + name.charCodeAt(i);
    }
    const index = Math.abs(hash % colors.length);
    return colors[index];
  }

  setNotificationConfig() {
    this.notification.config({
      nzPlacement: 'bottomRight',
      nzTop: '70px',
      nzDuration: 3000
    });

    return this.notification;
  }

  getUserInfo(userEmail) {
    if (!localStorage.getItem('accessToken')) {
      return;
    }

    return this.http.get(API_BASE_URL + '/user/' + userEmail);
  }

  getLineInfo(lineName) {
    return this.http.get(API_BASE_URL + '/auth/lines/' + lineName);
  }

  getChildInfo(childName) {
    return this.http.get(API_BASE_URL + '/auth/child/' + childName);
  }

  OnInputFilteredLinesForSearch(value: string): void {

    this.filteredOptions = [];
    this.linesList = [];

    this.linesList = JSON.parse(localStorage.getItem('lines'));

    this.filteredOptions = this.linesList.filter(linesList => linesList.toLowerCase().indexOf(value.toLowerCase()) === 0);
  }

  OnInputFilteredLines(value: string): void {
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));

    this.filteredOptions = [];
    this.linesList = [];

    if (currentUser.role === 'SYS_ADMIN') {
      this.linesList = JSON.parse(localStorage.getItem('lines'));
    } else {
      const currentUserLines = currentUser.lines;
      for (let i = 0; i < currentUserLines.length; i++) {
        this.linesList.push(currentUserLines[i].name);
      }
    }

    console.log(this.linesList);

    this.filteredOptions = this.linesList.filter(linesList => linesList.toLowerCase().indexOf(value.toLowerCase()) === 0);
  }

  OnInputFilteredChildren(value: string): void {

    const currentUserLines = JSON.parse(localStorage.getItem('currentUser')).children;
    this.filteredOptions = [];
    this.linesList = [];

    for (let i = 0; i < currentUserLines.length; i++) {
      this.linesList.push(currentUserLines[i].name);
    }

    this.filteredOptions_c = this.linesList.filter(linesList => linesList.toLowerCase().indexOf(value.toLowerCase()) === 0);
  }

  OnInputFilteredStopLines(value: string, line, num): void {

    this.filteredOptions = [];
    this.linesList = [];
    let currentLineStopLines = [];
    if (num === 0) {
      currentLineStopLines = line.stopLines_going;
    } else {
      currentLineStopLines = line.stopLines_return;
    }

    console.log(currentLineStopLines);
    for (let i = 0; i < currentLineStopLines.length; i++) {
      this.linesList.push(currentLineStopLines[i].stop.name);
    }

    this.filteredOptions = this.linesList.filter(linesList => linesList.toLowerCase().indexOf(value.toLowerCase()) === 0);
  }

  webSocketConnect() {

    const socket = new SockJS(API_BASE_URL + '/stomp');
    this.stompClient2 = Stomp.over(socket);
    const that = this;

    this.stompClient2.connect({}, function (frame) {
      that.stompClient2.subscribe('/topic/messages-' + that.getCurrentUserInfo().email, function (messageOutput) {
        that.showMessageOutput(JSON.parse(messageOutput.body));
      });
    });
  }

  showMessageOutput(messageOutput) {
    console.log(messageOutput);
    this.getCurrentUser();
    this.setAllChildren();
  }

  calculateNotificationCount(data) {
    console.log(data.user.messages.filter(d => d.read === false).length);
    return data.user.messages.filter(d => d.read === false).length;
  }

  setLinesList(lines: any) {
    this.linesList_ = [];
    for (let i = 0; i < lines.length; i++) {
      this.linesList_.push({
        id: i,
        name: lines[i].name
      });
    }
    console.log(this.linesList_);
    this.linesListObs.next(this.linesList_);
  }

  setAllNotifications() {
    this.http.get(API_BASE_URL + '/auth/messages/' + this.getCurrentUserInfo().id)
      .pipe(first())
      .subscribe(
        data => {},
        error => {}
      );

    this.updatesCountObs = 0;

    /*const currentUser_ = this.getCurrentUserInfo();
    for (let i = 0; i < currentUser_.messages.length; i++) {
      currentUser_.messages[i].read = true;
    }
    console.log(currentUser_);
    localStorage.setItem('currentUser', JSON.stringify(currentUser_));*/
  }

  setAllChildren() {
    this.getAllChildrenInfoAndReservations()
      .pipe(first())
      .subscribe(
        data => {
          this.setChildren(data);
          console.log(data);
        },
        error => {
          this.notification.error(
            'NApp',
            error || 'Sorry! Something went wrong. Please try again!'
          );
        });
  }

  setChildren(res) {
    console.log(JSON.parse(JSON.stringify(res)).childHacks);
    this.allChildren.next(JSON.parse(JSON.stringify(res)).childHacks);
  }

  holidays(date: Date) {
    // +1 in all months because index starts at 0???
    if (date.getMonth() === 6 || date.getMonth() === 7 || (date.getMonth() === 5 && date.getDate() > 15)
      || (date.getMonth() === 8 && date.getDate() < 15) || date.getFullYear() > this.now.getFullYear()) {
      return false;
    }
    return true;
  }

}
