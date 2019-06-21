import {Component, ElementRef, HostListener, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';

import { AuthService } from '../../app-auth/services/auth.service';
import { UserInfo } from '../../app-auth/models/user-info';
import {first} from 'rxjs/operators';
import {NzInputDirective, NzNotificationService} from 'ng-zorro-antd';
import {forEach} from '@angular/router/src/utils/collection';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {

  private currentUser: any;

  // data for line tab

  hasBeenEdited: boolean | false;
  hasError: boolean | false;
  editId: string | null;
  linesList: any[] = [];

  // data for children tab

  childrenList: any[] = [];
  hasBeenEdited_c: boolean | false;
  hasError_c: boolean | false;
  editId_c: string | null;

  constructor(
    private router: Router,
    private authService: AuthService,
    private route: ActivatedRoute,
    private notification: NzNotificationService
  ) { }

  ngOnInit() {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/']);
    }

    this.currentUser = this.authService.getCurrentUserInfo();

    this.linesList = this.authService.linesList_;

    this.authService.currentUser.subscribe((user: any) => {
      this.currentUser = user;
    });

    this.authService.linesListObs.subscribe((lines: any) => {
      this.linesList = lines;
    });

    console.log(this.linesList);

    console.log(this.currentUser);

    this.setUser();

    console.log(this.childrenList);
  }

  setUser() {
    this.childrenList = [];
    for (let i = 0; i < this.currentUser.children.length; i++) {
      this.childrenList.push({
        id: i,
        childName: this.currentUser.children[i].childName,
        status: '',
        old_name: this.currentUser.children[i].childName
      });
    }
  }

  getAvatarColor(name) {
    return this.authService.getAvatarColor(name);
  }

  // Functions for line tab

  addRow(): void {
    if (this.linesList.length === 0 || this.linesList[this.linesList.length - 1].name !== '') {
      this.linesList = [
        ...this.linesList,
        {
          id: this.linesList.length,
          name: ''
        }
      ];
    }
    this.hasBeenEdited = true;
  }

  deleteRow(id: string): void {
    if (this.currentUser.role === 'ADMIN') {
      const asd = this.currentUser.lines.filter(d => d.name === this.linesList[id].name );
      console.log(asd);
      if (asd.length === 0) {
        this.hasError = true;
        return;
      }
    }

    this.linesList = this.linesList.filter(d => d.id !== id);
    console.log(this.linesList);
    this.hasBeenEdited = true;
  }

  startEdit(id: string, event: MouseEvent): void {
    event.preventDefault();
    event.stopPropagation();
    if (this.currentUser.role === 'ADMIN' && this.linesList[id].name !== '') {
      const asd = this.currentUser.lines.filter(d => d.name === this.linesList[id].name );
      console.log(asd);
      if (asd.length === 0) {
        this.hasError = true;
        return;
      }
    }
    this.editId = id;
    this.hasBeenEdited = true;
  }

  finishEdit(id: string, event: MouseEvent): void {
    event.preventDefault();
    event.stopPropagation();
    if (this.currentUser.role === 'ADMIN' && this.linesList[id].name !== '') {
      const auth_check = this.currentUser.lines.filter(d => d.name === this.linesList[id].name );
      const duplicate_check = this.linesList.filter(d => d.name === this.linesList[id].name);
      console.log(duplicate_check);
      if (auth_check.length === 0 || duplicate_check.length > 0) {
        this.linesList[id].name = '';
        this.hasError = true;
      }
    }
    this.editId = null;
  }

  cancelChanges() {
    this.linesList = this.currentUser.lines;
    this.editId = null;
    this.hasBeenEdited = false;
  }

  validateChanges() {
    console.log(this.linesList);

    this.editId = null;

    this.currentUser.lines = this.linesList;
    localStorage.setItem('currentUser', JSON.stringify(this.currentUser));

    this.notification = this.authService.setNotificationConfig();

    this.authService.saveChanges(this.currentUser)
      .pipe(first())
      .subscribe(
        data => {
          this.notification.success(
            'PBus',
            'Changes saved!'
          );
        },
        error => {
          this.notification.error(
            'PBus',
            error || 'Sorry! Something went wrong. Please try again!'
          );
        });

    this.hasBeenEdited = false;
  }

  // Functions for children tab

  addRow_c(): void {
    if (this.childrenList.length === 0 || this.childrenList[this.childrenList.length - 1].name !== '') {
      this.childrenList = [
        ...this.childrenList,
        {
          id: this.childrenList.length,
          name: '',
          status: ''
        }
      ];
    }
    this.hasBeenEdited_c = true;
  }

  deleteRow_c(id: string): void {
    this.hasBeenEdited_c = true;
    this.editId_c = null;
    this.childrenList[id].status = 'deleted';
  }

  restoreRow_c(id: string): void {
    this.editId_c = null;
    this.childrenList[id].status = 'updated';
  }

  startEdit_c(id: string, event: MouseEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.editId_c = id;
    this.hasBeenEdited_c = true;
  }

  finishEdit_c(id: string, event: MouseEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.editId_c = null;
    this.childrenList[id].status = 'updated';
  }

  cancelChanges_c() {
    this.setUser();
    console.log(this.childrenList);
    this.editId_c = null;
    this.hasBeenEdited_c = false;
  }

  validateChanges_c() {
    this.editId_c = null;

    for (let i = 0; i < this.currentUser.children.length; i++) {
      this.childrenList[i].id = null;
    }

    this.currentUser.children = this.childrenList;
    localStorage.setItem('currentUser', JSON.stringify(this.currentUser));
    console.log(this.childrenList);

    this.notification = this.authService.setNotificationConfig();

    this.authService.saveChanges(this.currentUser)
      .pipe(first())
      .subscribe(
        data => {
          this.notification.success(
            'PBus',
            'Changes saved!'
          );
          this.updateCurrentUser();
          this.router.navigate(['/'], { skipLocationChange: true })
            .then(() => this.router.navigate(['/profile', { select: 1 }]));
        },
        error => {
          this.notification.error(
            'PBus',
            error || 'Sorry! Something went wrong. Please try again!'
          );
        });

    console.log(this.editId_c);

    this.hasBeenEdited_c = false;
  }

  updateCurrentUser() {
    for (let i = 0; i < this.childrenList.length; i++) {
      if (this.childrenList[i].status === 'deleted') {
        this.currentUser.children = this.currentUser.children.filter(d => d.childName !== this.childrenList[i].childName );
      }
    }
    localStorage.setItem('currentUser', JSON.stringify(this.currentUser));
  }

}
