import { Component } from '@angular/core';
import { AuthService } from '@auth0/auth0-angular';
import { LoadingComponent } from './../../components/loading/loading.component';
import { AsyncPipe, NgIf } from '@angular/common';
import {DashboardComponent} from "../dashboard/dashboard.component";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  standalone: true,
  imports: [
    LoadingComponent,
    AsyncPipe,
    NgIf,
    DashboardComponent
  ]
})
export class HomeComponent {
  constructor(public auth: AuthService) {}
}
