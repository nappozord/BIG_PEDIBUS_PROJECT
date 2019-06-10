import { Component, OnInit } from '@angular/core';
import {AuthService} from '../app-auth/services/auth.service';
import {NzNotificationService} from 'ng-zorro-antd';
import {ActivatedRoute, Router} from '@angular/router';
import {first} from 'rxjs/operators';
import {current} from 'codelyzer/util/syntaxKind';

@Component({
  selector: 'app-app-line',
  templateUrl: './app-all-line.component.html',
  styleUrls: ['./app-all-line.component.css']
})
export class AppAllLineComponent implements OnInit {

  private currentUser;
  private allLines: any[];
  private response: any;
  private currentChildName: string;

  constructor(
    private authService: AuthService,
    private notification: NzNotificationService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit() {
    this.notification = this.authService.setNotificationConfig();

    this.currentUser = this.authService.getCurrentUserInfo();

    this.authService.currentUser.subscribe((user: any) => {
      this.currentUser = user;
    });

    this.route.params.subscribe(params => {
      this.currentChildName = params['child'];
    });

    console.log(this.currentChildName);

    this.authService.getallLinesInfo()
      .pipe(first())
      .subscribe(
        data => {
          this.setLines(data);
          console.log(this.allLines);
        },
        error => {
          this.notification.error(
            'PBus',
            error || 'Sorry! Something went wrong. Please try again!'
          );
        });
  }

  setLines(res) {
    this.response = JSON.parse(JSON.stringify(res));
    this.allLines = this.response.lines;
  }

  goToLine(line: any) {
    this.router.navigate(['/lines', line.name]);
  }

  goToLineSub(line: any) {
    this.router.navigate(['/lines', line.name, this.currentChildName]);
  }

  getFrom(line: any): string {
    return line.stopLines_going[0].stop.name;
  }

  getDest(line: any): string {
    return line.stopLines_return[0].stop.name;
  }

  amAdmin(line: any) {
    if (this.currentUser.role === 'SYS_ADMIN') {
      return true;
    }
    for (let i = 0; i < this.currentUser.lines.length; i++) {
      if (this.currentUser.lines[i].name === line.name) {
        return true;
      }
    }
    return false;
  }

}
