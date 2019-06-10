import {Component, Injectable, OnInit} from '@angular/core';
import {AuthService} from '../app-auth/services/auth.service';
import {UserInfo} from '../app-auth/models/user-info';
import {first, shareReplay, tap} from 'rxjs/operators';
import {NzNotificationService} from 'ng-zorro-antd';
import {Router} from '@angular/router';
import {Response} from '../app-auth/models/response';

@Component({
  selector: 'app-app-all-users',
  templateUrl: './app-all-users.component.html',
  styleUrls: ['./app-all-users.component.css']
})
export class AppAllUsersComponent implements OnInit {

  private allUsers: any[];
  private response: Response;

  constructor(
    private authService: AuthService,
    private notification: NzNotificationService,
    private router: Router
  ) { }

  ngOnInit() {
    this.notification = this.authService.setNotificationConfig();

    if (this.authService.getCurrentUserInfo().role === 'USER') {
      this.router.navigate(['/']);
    }

    this.authService.getAllUsersInfo()
      .pipe(first())
      .subscribe(
        data => {
          this.setUsers(data);
          console.log(this.allUsers);
        },
        error => {
          this.notification.error(
            'PBus',
            error || 'Sorry! Something went wrong. Please try again!'
          );
        });
  }

  setUsers(res) {
    this.response = JSON.parse(JSON.stringify(res)) as Response;
    this.allUsers = this.response.users;
  }

  getAvatarColor(name) {
    return this.authService.getAvatarColor(name);
  }

  goToUser(user: UserInfo) {
    if (user.email === this.authService.getCurrentUserInfo().email) {
      this.router.navigate(['/profile']);
    } else {
      this.router.navigate(['/profile', user.email]);
    }
  }

}
