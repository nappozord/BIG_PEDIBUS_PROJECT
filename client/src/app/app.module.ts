import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';

import { ToastrModule } from 'ngx-toastr';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { AppAuthModule } from './app-auth/app-auth.module';
import { AppBootstrapModule } from './app-bootstrap/app-bootstrap.module';
import { AppUserModule } from './app-user/app-user.module';
import { AppAlertModule } from './app-alert/app-alert.module';
import { HomeComponent } from './app-home/home.component';
import { NgZorroAntdModule, NZ_I18N, en_US } from 'ng-zorro-antd';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import { registerLocaleData } from '@angular/common';
import en from '@angular/common/locales/en';
import { SignupFromAdminComponent } from './app-user/signup-from-admin/signup-from-admin.component';
import { AppAllUsersComponent } from './app-all-users/app-all-users.component';
import { AppAllLineComponent } from './app-all-line/app-all-line.component';
import { LineComponent } from './app-all-line/line/line.component';
import { AppReservationComponent } from './app-reservation/app-reservation.component';
import { AppAllChildrenComponent } from './app-all-children/app-all-children.component';
import { ChildComponent } from './app-all-children/child/child.component';
import { ModalRefComponent } from './app-all-children/child/modal-ref/modal-ref.component';
import { AppTurnsComponent } from './app-turns/app-turns.component';
import { ModalTurnComponent } from './app-turns/modal-turn/modal-turn.component';
import { AppTurnsPresencesComponent } from './app-turns-presences/app-turns-presences.component';
import { ModalTurnPresencesComponent } from './app-turns-presences/modal-turn-presences/modal-turn-presences.component';

registerLocaleData(en);

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    SignupFromAdminComponent,
    AppAllUsersComponent,
    AppAllLineComponent,
    LineComponent,
    AppReservationComponent,
    AppAllChildrenComponent,
    ChildComponent,
    ModalRefComponent,
    AppTurnsComponent,
    ModalTurnComponent,
    AppTurnsPresencesComponent,
    ModalTurnPresencesComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    ToastrModule.forRoot(),
    AppRoutingModule,
    AppAuthModule,
    AppBootstrapModule,
    AppUserModule,
    AppAlertModule,
    NgZorroAntdModule,
    FormsModule,
    ReactiveFormsModule
  ],
  entryComponents: [
    ModalRefComponent,
    ChildComponent
  ],
  providers: [{ provide: NZ_I18N, useValue: en_US }],
  bootstrap: [AppComponent]
})
export class AppModule { }
