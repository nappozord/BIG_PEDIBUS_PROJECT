import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { LoginComponent } from './app-user/login/login.component';
import { SignupComponent } from './app-user/signup/signup.component';
import { ProfileComponent } from './app-user/profile/profile.component';
import { HomeComponent } from './app-home/home.component';
import { Oauth2Component } from'./app-user/oauth2/oauth2.component';
import {RegistrationComponent} from './app-user/registration/registration.component';
import {PassResetComponent} from './app-user/pass-reset/pass-reset.component';
import {RecoveryConfirmComponent} from './app-user/recovery-confirm/recovery-confirm.component';
import {SignupFromAdminComponent} from './app-user/signup-from-admin/signup-from-admin.component';
import {AppAllUsersComponent} from './app-all-users/app-all-users.component';
import {UserProfileComponent} from './app-user/profile/user-profile/user-profile.component';
import {AppAllLineComponent} from './app-all-line/app-all-line.component';
import {LineComponent} from './app-all-line/line/line.component';
import {AppReservationComponent} from './app-reservation/app-reservation.component';
import {AppAllChildrenComponent} from './app-all-children/app-all-children.component';
import {ChildComponent} from './app-all-children/child/child.component';
import {AppTurnsComponent} from './app-turns/app-turns.component';
import {AppTurnsPresencesComponent} from './app-turns-presences/app-turns-presences.component';

const routes: Routes = [
  {
    path: '',
    children: [
      { path: '', component: HomeComponent },
      { path: 'login', component: LoginComponent },
      { path: 'signup', component: SignupFromAdminComponent },
      { path: 'confirmprofile', component: SignupComponent },
      { path: 'profile', children: [
          { path: '', component: ProfileComponent},
          { path: ':user', component:  UserProfileComponent}
        ]
      },
      { path: 'lines', children: [
          { path: '', component: AppAllLineComponent },
          { path: ':line', component: LineComponent },
          { path: ':line/:child', component: LineComponent }
        ]
      },
      { path: 'children', children: [
          { path: '', component: AppAllChildrenComponent },
          { path: ':child', component: ChildComponent }
        ]
      },
      { path: 'newreservation', component: AppReservationComponent},
      { path: 'oauth2/redirect', component: Oauth2Component },
      { path: 'passwordrecovery', component: PassResetComponent },
      { path: 'recoveryconfirm', component: RecoveryConfirmComponent },
      { path: 'registrationconfirm', component: RegistrationComponent },
      { path: 'listallusers', component: AppAllUsersComponent },
      { path: 'turns', component: AppTurnsComponent },
      { path: 'manageturns', component: AppTurnsPresencesComponent }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
