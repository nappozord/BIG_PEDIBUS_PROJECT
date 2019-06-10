import {Component, HostListener, OnInit} from '@angular/core';
import { Router } from '@angular/router';

import { AuthService } from './app-auth/services/auth.service';
import {UserInfo} from './app-auth/models/user-info';
import {NzNotificationService} from 'ng-zorro-antd';
import {FormBuilder, FormGroup} from '@angular/forms';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  validateForm: FormGroup;
  private currentUser: any;
  private messageIconSelected = false;
  public innerWidth: any;
  private isMobile = false;
  isCollapsed = false;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    public authService: AuthService,
    private notification: NzNotificationService
  ) { }

  ngOnInit() {
    this.innerWidth = window.innerWidth;

    if ( /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) ) {
      this.isMobile = true;
      this.isCollapsed = true;
      console.log('mobile device detected');
    }

    if (this.authService.isLoggedIn()) {
      this.authService.webSocketConnect();
      this.authService.getCurrentUser();
    }

    this.validateForm = this.formBuilder.group({
      line: []
    });

    this.notification = this.authService.setNotificationConfig();

    this.authService.currentUser.subscribe((user: any) => {
      this.currentUser = user;
    });

    console.log(this.currentUser);
  }

  @HostListener('window:resize', ['$event'])
  onResize(event) {
    this.innerWidth = window.innerWidth;
  }

  logout() {

    this.authService.logout();
    this.router.navigate(['/']);
    this.notification.success(
      'PBus',
      'You\'re safely logged out!'
    );
  }

  getCurrentUser(): any {
    this.currentUser = JSON.parse(localStorage.getItem('currentUser')) as UserInfo;
    return this.currentUser;
  }

  getAvatarColor(name) {
    return this.authService.getAvatarColor(name);
  }

  getSiderWidth() {
    if (this.isMobile) {
      return this.innerWidth - 10;
    } else {
      return 250;
    }
  }

  close_or_not() {
    if (this.isMobile) {
      this.isCollapsed = true;
    }
  }

  centerContent() {
    if (!this.isMobile) {
      if (this.isCollapsed) {
        return 0;
      }
      return 15;
    }
    return 3;
  }

  resetNewNotificationCount() {
    if (this.authService.updatesCountObs !== 0) {
      this.authService.setAllNotifications();
    }
  }

  goToLine($event, line: any) {
    $event.preventDefault();
    console.log(this.validateForm.get('line').value.toString());
    this.router.navigate(['/lines', this.validateForm.get('line').value.toString()]);
  }

}
