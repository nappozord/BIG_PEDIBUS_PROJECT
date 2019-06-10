import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormControl, FormGroup, ValidationErrors, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {AuthService} from '../../app-auth/services/auth.service';
import {NzNotificationService} from 'ng-zorro-antd';
import {first} from 'rxjs/operators';
import {Observable, Observer} from 'rxjs';

@Component({
  selector: 'app-pass-reset',
  templateUrl: './pass-reset.component.html',
  styleUrls: ['./pass-reset.component.css']
})
export class PassResetComponent implements OnInit {

  validateForm: FormGroup;
  isloading = false;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private authService: AuthService,
    private notification: NzNotificationService
  ) { }

  submitForm = ($event: any, value: any) => {
    $event.preventDefault();
    // stop here if form is invalid
    if (this.validateForm.invalid) {
      return;
    }

    this.notification = this.authService.setNotificationConfig();

    this.isloading = true;

    this.authService.pass_recovery(this.validateForm.controls.email.value, this.validateForm.controls.password.value)
      .pipe(first())
      .subscribe(
        data => {
          this.notification.success(
            'PBus',
            'Thank you! Now confirm your new password!'
          );
          this.router.navigate(['/recoveryconfirm']);
        },
        error => {
          this.isloading = false;
          this.notification.error(
            'PBus',
            error || 'Sorry! Something went wrong. Please try again!'
          );
        });
  }

  validateConfirmPassword(): void {
    setTimeout(() => this.validateForm.controls.confirm.updateValueAndValidity());
  }

  confirmValidator = (control: FormControl): { [s: string]: boolean } => {
    if (!control.value) {
      return { required: true };
    } else if (control.value !== this.validateForm.controls.password.value) {
      return { confirm: true, error: true };
    }
    return {};
  }

  ngOnInit() {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/']);
    }

    this.validateForm = this.formBuilder.group({
      email: ['', [Validators.email, Validators.required]],
      password: ['', [Validators.required]],
      confirm: ['', [this.confirmValidator]]
    });
  }

}
