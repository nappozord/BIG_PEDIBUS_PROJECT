import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule  } from '@angular/forms';
import { AvatarModule } from 'ng2-avatar';

import { AppBootstrapModule } from './../app-bootstrap/app-bootstrap.module';

import { LoginComponent } from './login/login.component';
import { ProfileComponent } from './profile/profile.component';
import { SignupComponent } from './signup/signup.component';
import {
  NzAlertModule, NzAutocompleteModule,
  NzAvatarModule,
  NzButtonModule,
  NzDividerModule,
  NzFormModule,
  NzIconModule,
  NzInputModule,
  NzListModule, NzPopconfirmModule,
  NzSpinModule, NzTableModule, NzTabsModule
} from 'ng-zorro-antd';
import { Oauth2Component } from './oauth2/oauth2.component';
import { RegistrationComponent } from './registration/registration.component';
import { PassResetComponent } from './pass-reset/pass-reset.component';
import { RecoveryConfirmComponent } from './recovery-confirm/recovery-confirm.component';
import { UserProfileComponent } from './profile/user-profile/user-profile.component';

@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule,
    AvatarModule,
    AppBootstrapModule,
    NzFormModule,
    NzInputModule,
    NzButtonModule,
    NzIconModule,
    NzDividerModule,
    NzAvatarModule,
    NzSpinModule,
    NzListModule,
    NzTableModule,
    NzPopconfirmModule,
    NzAlertModule,
    NzTabsModule,
    NzAutocompleteModule
  ],
  declarations: [LoginComponent, ProfileComponent, SignupComponent, Oauth2Component, RegistrationComponent, PassResetComponent, RecoveryConfirmComponent, UserProfileComponent]
})
export class AppUserModule { }
