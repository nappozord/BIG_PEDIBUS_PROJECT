import { Component, OnInit } from '@angular/core';
import {AuthService} from '../../app-auth/services/auth.service';
import {Router} from '@angular/router';

import { first } from 'rxjs/internal/operators/first';
import {NzNotificationService} from 'ng-zorro-antd';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent implements OnInit {

  private token;
  private email;
  constructor(
    private router: Router,
    private authService: AuthService,
    private notification: NzNotificationService
  ) { }

  ngOnInit() {
    this.token = this.getUrlParameter('token');
    if (this.token) {
      localStorage.setItem('token', this.token);
      this.router.navigate(['confirmprofile']);
    } else if (!localStorage.getItem('email')) {
      this.router.navigate(['/']);
    }

    this.notification = this.authService.setNotificationConfig();

    this.email = localStorage.getItem('email');
  }

  getUrlParameter(name) {
    name = name.replace(/[\[]/, '\\[').replace(/[\]]/, '\\]');
    const regex = new RegExp('[\\?&]' + name + '=([^&#]*)');
    const results = regex.exec(location.search);
    return results === null ? '' : decodeURIComponent(results[1].replace(/\+/g, ' '));
  }

  submitForm_1 = ($event: any) => {
    $event.preventDefault();
    this.authService.emailConfirmationReSend(this.email)
      .pipe(first())
      .subscribe(
        data => {
          this.notification.success(
            'PBus',
            'Confirmation email re-sent.'
          );
        },
        error => {
          this.notification.error(
            'PBus',
            error || 'Sorry! Something went wrong. Please try again!'
          );
        }
      );
  }

}
