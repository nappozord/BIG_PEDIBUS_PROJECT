import { Component, OnInit } from '@angular/core';
import {AuthService} from '../app-auth/services/auth.service';
import {NzNotificationService} from 'ng-zorro-antd';
import {Router} from '@angular/router';
import {first} from 'rxjs/operators';

@Component({
  selector: 'app-app-all-children',
  templateUrl: './app-all-children.component.html',
  styleUrls: ['./app-all-children.component.css']
})
export class AppAllChildrenComponent implements OnInit {

  private allChildren: any[];
  private response: any;

  constructor(
    private authService: AuthService,
    private notification: NzNotificationService,
    private router: Router
  ) { }

  ngOnInit() {
    this.notification = this.authService.setNotificationConfig();

    this.authService.getAllChildrenInfo()
      .pipe(first())
      .subscribe(
        data => {
          this.setChildren(data);
          console.log(this.allChildren);
        },
        error => {
          this.notification.error(
            'NApp',
            error || 'Sorry! Something went wrong. Please try again!'
          );
        });
  }

  setChildren(res) {
    this.response = JSON.parse(JSON.stringify(res));
    this.allChildren = this.response.children;
  }

  getAvatarColor(name) {
    return this.authService.getAvatarColor(name);
  }

  goToChild(child: any) {
    this.router.navigate(['/children', child.childName]);
  }

  hasNotDefaultYet(child) {
    if (child.lineDefault === null) {
      return true;
    }
    return false;
  }

}
