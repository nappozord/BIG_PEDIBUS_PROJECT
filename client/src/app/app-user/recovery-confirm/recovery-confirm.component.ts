import { Component, OnInit } from '@angular/core';
import {Router} from '@angular/router';
import {AuthService} from '../../app-auth/services/auth.service';
import {NzNotificationService} from 'ng-zorro-antd';
import {first} from 'rxjs/operators';

@Component({
  selector: 'app-recovery-confirm',
  templateUrl: './recovery-confirm.component.html',
  styleUrls: ['./recovery-confirm.component.css']
})
export class RecoveryConfirmComponent implements OnInit {

  private token;
  private email;
  constructor(
    private router: Router,
    private authService: AuthService,
    private notification: NzNotificationService
  ) { }

  ngOnInit() {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/']);
    }

    this.notification = this.authService.setNotificationConfig();

    this.token = this.getUrlParameter('token');
    this.email = localStorage.getItem('email');
  }

  hasToken(): boolean {
    if (!this.token) {
      return false;
    }
    return true;
  }

  getUrlParameter(name) {
    name = name.replace(/[\[]/, '\\[').replace(/[\]]/, '\\]');
    const regex = new RegExp('[\\?&]' + name + '=([^&#]*)');
    const results = regex.exec(location.search);
    return results === null ? '' : decodeURIComponent(results[1].replace(/\+/g, ' '));
  }

  submitForm_1 = ($event: any) => {
    $event.preventDefault();
    this.router.navigate(['/']);
  }

  submitForm_2 = ($event: any) => {
    $event.preventDefault();
    this.authService.recoveryConfirmation(this.token).
    pipe(first())
      .subscribe(
        data => {
          this.notification.success(
            'NApp',
            'Thank you! Your email is confirmed'
          );
          this.router.navigate(['/']);
        },
        error => {
          this.notification.error(
            'NApp',
            error || 'Sorry! Something went wrong. Please try again!'
          );
        }
      );
  }

}
