import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {AuthService} from '../../app-auth/services/auth.service';
import {NzNotificationService} from 'ng-zorro-antd';
import {first} from 'rxjs/operators';

@Component({
  selector: 'app-signup-from-admin',
  templateUrl: './signup-from-admin.component.html',
  styleUrls: ['./signup-from-admin.component.css']
})
export class SignupFromAdminComponent implements OnInit {

  validateForm: FormGroup;
  isloading = false;
  hasError: boolean;

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

    const currentUser = this.authService.getCurrentUserInfo();

    if (currentUser.role === 'ADMIN') {
      const asd = currentUser.lines.filter(d => d.name === this.validateForm.get('line').value.toString());
      console.log(asd);
      if (asd.length === 0) {
        this.hasError = true;
        return;
      }
    }

    this.isloading = true;

    if (this.validateForm.get('role').value !== 'ADMIN') {
      this.validateForm.get('line').setValue('');
    }

    this.authService.register_from_admin(this.validateForm.value)
      .pipe(first())
      .subscribe(
        data => {
          this.notification.success(
            'NApp',
            'Thank you! You\'re successfully registered. Confirm your email!'
          );
          this.router.navigate(['/registrationconfirm']);
        },
        error => {
          this.isloading = false;
          this.notification.error(
            'NApp',
            error || 'Sorry! Something went wrong. Please try again!'
          );
        });
  }

  ngOnInit() {
    this.notification = this.authService.setNotificationConfig();

    if (!this.authService.isLoggedIn() || this.authService.getCurrentUserInfo().role === 'USER') {
      this.router.navigate(['/']);
    }

    this.validateForm = this.formBuilder.group({
      email: ['', [Validators.email, Validators.required]],
      role: ['USER', [Validators.required]],
      line: ['NO-LINE', [Validators.required]]
    });

    this.hasError = false;
  }

  isAdmin() {
    if (this.validateForm.get('role').value === 'ADMIN') {
      if (this.validateForm.get('line').value === 'NO-LINE') {
        this.validateForm.get('line').setValue('');
      }
      return true;
    }
    this.validateForm.get('line').setValue('NO-LINE');
    return false;
  }

}
