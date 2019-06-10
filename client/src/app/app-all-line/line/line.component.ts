import { Component, OnInit } from '@angular/core';
import {AuthService} from '../../app-auth/services/auth.service';
import {ActivatedRoute, Router} from '@angular/router';
import {first} from 'rxjs/operators';
import {NzNotificationService} from 'ng-zorro-antd';

@Component({
  selector: 'app-line',
  templateUrl: './line.component.html',
  styleUrls: ['./line.component.css']
})
export class LineComponent implements OnInit {

  private currentLine: any;
  private currentLineName: string;
  private currentChildName: string;
  private stopsList_going: any[] = [];
  private stopsList_return: any[] = [];
  private admins: any[] = [];
  private disabled: boolean;
  private stopGoingDefault = [];
  private stopReturnDefault = [];
  private selectIndex;

  constructor(
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    private notification: NzNotificationService
  ) { }

  ngOnInit() {

    this.notification = this.authService.setNotificationConfig();

    this.route.params.subscribe(params => {
      this.currentLineName = params['line'];
    });

    this.route.params.subscribe(params => {
      this.currentChildName = params['child'];
    });

    if (this.currentChildName !== undefined) {
      this.disabled = true;
    } else {
      this.disabled = undefined;
    }

    this.selectIndex = 0;

    console.log(this.currentChildName);

    this.authService.getLineInfo(this.currentLineName)
      .pipe(first())
      .subscribe(
        data => {
          this.setLine(data);
          console.log(this.currentLine);
        },
        error => {
          this.notification.error(
            'PBus',
            'The line does not exist'
          );
          this.router.navigate(['/']);
        });
  }

  setLine(res) {
    this.currentLine = JSON.parse(JSON.stringify(res)).line;

    this.admins = JSON.parse(JSON.stringify(res)).admins;

    console.log(this.admins);

    console.log(this.currentLine);

    for (let i = 0; i < this.currentLine.stopLines_going.length; i++) {
      this.stopsList_going.push({
        id:  this.currentLine.stopLines_going[i].id,
        stop: this.currentLine.stopLines_going[i].stop,
        hour: this.currentLine.stopLines_going[i].hour
      });
    }

    console.log(this.stopsList_going);

    for (let i = 0; i < this.currentLine.stopLines_return.length; i++) {
      this.stopsList_return.push({
        id: this.currentLine.stopLines_return[i].id,
        stop: this.currentLine.stopLines_return[i].stop,
        hour: this.currentLine.stopLines_return[i].hour
      });
    }
  }

  getFrom(): string {
    return this.currentLine.stopLines_going[0].stop.name;
  }

  getDest(): string {
    return this.currentLine.stopLines_return[0].stop.name;
  }

  goToUser(user: any) {
    if (user.email === this.authService.getCurrentUserInfo().email) {
      this.router.navigate(['/profile']);
    } else {
      this.router.navigate(['/profile', user.email]);
    }
  }

  setStopGoingDefault(data) {
    this.stopGoingDefault = data;
    this.disabled = !this.disabled;
    this.selectIndex = 1;
  }

  submitDefault(data) {
    this.stopReturnDefault = data;
    const options = {
      name: this.currentChildName,
      line: this.currentLineName,
      stop_going: this.stopGoingDefault,
      stop_return: this.stopReturnDefault
    };

    console.log(options);

    this.authService.setDefault(options)
      .pipe(first())
      .subscribe(
        res => {
          this.notification.success(
            'PBus',
            'You successfully subscribed to this line'
          );
          this.router.navigate(['/']);
        },
        error => {
          this.notification.error(
            'PBus',
            error || 'Something went wrong'
          );
          this.router.navigate(['/']);
        });
  }

  isDisabled() {
    if (this.disabled === undefined) {
      return false;
    } else {
      return this.disabled;
    }
  }


}
