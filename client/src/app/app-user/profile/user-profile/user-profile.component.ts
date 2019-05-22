import {Component, ElementRef, HostListener, OnInit, ViewChild} from '@angular/core';
import {UserInfo} from '../../../app-auth/models/user-info';
import {AuthService} from '../../../app-auth/services/auth.service';
import {ActivatedRoute, Router} from '@angular/router';
import {first} from 'rxjs/operators';
import {NzInputDirective, NzNotificationService} from 'ng-zorro-antd';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css']
})
export class UserProfileComponent implements OnInit {

  private currentUser: any;
  private profileUser: any;
  private profileUserEmail: string;
  private currentUserLines: string[] = [];
  hasBeenEdited: boolean | false;
  hasError: boolean | false;
  editId: string | null;
  linesList: any[] = [];

  constructor(
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    private notification: NzNotificationService
  ) { }

  ngOnInit() {
    if (!this.authService.isLoggedIn() || this.authService.getCurrentUserInfo().role === 'USER') {
      this.router.navigate(['/']);
    }

    this.currentUser = this.authService.getCurrentUserInfo();

    if (this.currentUser.role === 'ADMIN') {
      this.currentUserLines = this.currentUser.lines;
      console.log(this.currentUserLines);
    }

    this.route.params.subscribe(params => {
      this.profileUserEmail = params['user'];
    });

    this.authService.getUserInfo(this.profileUserEmail)
      .pipe(first())
      .subscribe(
        data => {
          this.setUser(data);
          console.log(this.profileUser);
        },
        error => {
          this.notification.error(
            'NApp',
            error || 'Sorry! Something went wrong. Please try again!'
          );
        });
  }

  setUser(res) {
    this.profileUser = JSON.parse(JSON.stringify(res)) as UserInfo;

    for (let i = 0; i < this.profileUser.lines.length; i++) {
      this.linesList.push({
        id: i,
        name: this.profileUser.lines[i].name
      });
    }
  }

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
    this.hasBeenEdited = true;
  }

  startEdit(id: string, event: MouseEvent): void {
    if (this.currentUser.role === 'ADMIN' && this.linesList[id].name !== '') {
      const asd = this.currentUser.lines.filter(d => d.name === this.linesList[id].name );
      console.log(asd);
      if (asd.length === 0) {
        this.hasError = true;
        return;
      }
    }

    event.preventDefault();
    event.stopPropagation();
    this.editId = id;
    this.hasBeenEdited = true;
  }

  finishEdit(id: string, event: MouseEvent): void {
    if (this.currentUser.role === 'ADMIN' && this.linesList[id].name !== '') {
      const auth_check = this.currentUser.lines.filter(d => d.name === this.linesList[id].name );
      const duplicate_check = this.linesList.filter(d => d.name === this.linesList[id].name);
      console.log(duplicate_check);
      if (auth_check.length === 0 || duplicate_check.length > 0) {
        this.linesList[id].name = '';
        this.hasError = true;
      }
    }

    event.preventDefault();
    event.stopPropagation();
    this.editId = null;
  }

  getAvatarColor(name) {
    return this.authService.getAvatarColor(name);
  }

  cancelChanges() {
    this.linesList = this.profileUser.lines;
    this.editId = null;
    this.hasBeenEdited = false;
  }

  validateChanges() {

    this.profileUser.lines = this.linesList;

    this.notification = this.authService.setNotificationConfig();

    this.authService.saveChanges(this.profileUser)
      .pipe(first())
      .subscribe(
        data => {
          this.notification.success(
            'NApp',
            'Changes saved!'
          );
          this.router.navigate(['listallusers']);
        },
          error => {
            this.notification.error(
              'NApp',
              error || 'Sorry! Something went wrong. Please try again!'
            );
          });

    this.hasBeenEdited = false;
  }

}
