import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators, ValidationErrors, FormControl} from '@angular/forms';
import { first } from 'rxjs/operators';
import { Router } from '@angular/router';
import { AuthService } from '../../app-auth/services/auth.service';
import {Observable, Observer} from 'rxjs';
import {NzNotificationService} from 'ng-zorro-antd';
import {environment} from '../../../environments/environment';

const GOOGLE_AUTH_URL = environment.GOOGLE_AUTH_URL;
const FACEBOOK_AUTH_URL = environment.FACEBOOK_AUTH_URL;

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit {

  validateForm: FormGroup;
  isloading = false;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private authService: AuthService,
    private notification: NzNotificationService
  ) { }

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

    this.isloading = true;

    this.authService.register(this.validateForm.value)
      .pipe(first())
      .subscribe(
        data => {
          this.notification.success(
            'NApp',
            'Thank you! Your profile has been updated!'
          );
          localStorage.removeItem('token');
          this.router.navigate(['/login']);
        },
        error => {
          this.isloading = false;
          this.notification.error(
            'NApp',
            error || 'Sorry! Something went wrong. Please try again!'
          );
        });
  }

  validateConfirmPassword(): void {
    setTimeout(() => this.validateForm.controls.confirm.updateValueAndValidity());
  }

  userNameAsyncValidator = (control: FormControl) =>
    new Observable((observer: Observer<ValidationErrors | null>) => {
      setTimeout(() => {
        if (control.value === 'JasonWood') {
          observer.next({ error: true, duplicated: true });
        } else {
          observer.next(null);
        }
        observer.complete();
      }, 1000);
    })

  confirmValidator = (control: FormControl): { [s: string]: boolean } => {
    if (!control.value) {
      return { required: true };
    } else if (control.value !== this.validateForm.controls.password.value) {
      return { confirm: true, error: true };
    }
    return {};
  }

  ngOnInit() {
    if (this.authService.isLoggedIn() || !localStorage.getItem('token')) {
      this.router.navigate(['/']);
    }

    this.validateForm = this.formBuilder.group({
      name: ['', [Validators.required], [this.userNameAsyncValidator]],
      password: ['', [Validators.required]],
      confirm: ['', [this.confirmValidator]]
    });
  }

}
