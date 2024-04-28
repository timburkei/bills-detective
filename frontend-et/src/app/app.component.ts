import {Component, OnInit} from '@angular/core';
import {RouterLink, RouterOutlet} from '@angular/router';
import { NavBarComponent } from './components/nav-bar/nav-bar.component';
import {AuthService} from "@auth0/auth0-angular";
import {AsyncPipe, NgIf} from "@angular/common";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  standalone: true,
  imports: [RouterOutlet, NavBarComponent, AsyncPipe, NgIf, RouterLink],
})
export class AppComponent implements OnInit {
  title = 'Bills Detective';

  profileJson: string = null;

  constructor(public auth: AuthService) {}

  ngOnInit() {
    this.auth.user$.subscribe(
      (profile) => (this.profileJson = JSON.stringify(profile, null, 2))
    );
  }
}
