import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { first } from 'rxjs/internal/operators/first';

import { environment } from 'src/environments/environment';

import {NzNotificationService} from 'ng-zorro-antd';
import {AuthService} from '../../app-auth/services/auth.service';

const GOOGLE_AUTH_URL = environment.GOOGLE_AUTH_URL;
const FACEBOOK_AUTH_URL = environment.FACEBOOK_AUTH_URL;

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  validateForm: FormGroup;
  isloading = false;
  passwordVisible = false;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private authService: AuthService,
    private notification: NzNotificationService
    ) { }

  ngOnInit() {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/']);
    }

    this.validateForm = this.formBuilder.group({
      email: ['', [Validators.email, Validators.required]],
      password: ['', [Validators.required]]
    });
  }

  onClickGoogle() {
    location.href = GOOGLE_AUTH_URL;
  }

  onClickFacebook() {
    location.href = FACEBOOK_AUTH_URL;
  }

  submitForm = ($event: any, value: any) => {
    $event.preventDefault();
    // stop here if form is invalid
    if (this.validateForm.invalid) {
      return;
    }

    this.notification = this.authService.setNotificationConfig();

    this.authService.login(this.validateForm.controls.email.value, this.validateForm.controls.password.value)
      .pipe(first())
      .subscribe(
        data => {
          this.notification.success(
            'PBus',
            'You\'re successfully logged in!'
          );
          this.router.navigate(['/']);
        },
        error => {
          if (error.toString() === 'Email not yet confirmed.') {
            this.notification.error(
              'PBus',
              error || 'Sorry! Something went wrong. Please try again!'
            );
            this.isloading = true;
            this.authService.emailConfirmationReSend(this.validateForm.controls.email.value)
              .pipe(first())
              .subscribe(
                data => {
                  this.isloading = false;
                  this.notification.success(
                    'PBus',
                    'Confirmation email re-sent.'
                  );
                },
                // tslint:disable-next-line:no-shadowed-variable
                error => {
                  this.isloading = false;
                  this.notification.error(
                    'PBus',
                    error || 'Sorry! Something went wrong. Please try again!'
                  );
                }
              );
          } else {
            this.notification.error(
              'PBus',
              error || 'Sorry! Something went wrong. Please try again!'
            );
          }
        });
  }

}
