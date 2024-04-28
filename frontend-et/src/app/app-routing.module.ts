import {
  Routes,
} from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { authGuardFn } from '@auth0/auth0-angular';
import {NotFoundComponent} from "./pages/not-found/not-found.component";
import {WelcomePageComponent} from "./pages/welcome-page/welcome-page.component";
import {CreateChartComponent} from "./pages/create-chart/create-chart.component";
import {ReceiptHistoryComponent} from "./pages/receipt-history/receipt-history.component";
import {ExpensesComponent} from "./pages/expenses/expenses.component";

export const routes: Routes = [
  {
    path: 'profile',
    component: ProfileComponent,
    canActivate: [authGuardFn],
  },
  {
    path: 'expenses',
    component: ExpensesComponent,
    canActivate: [authGuardFn],
  },
  {
    path: 'charts',
    component: CreateChartComponent,
    canActivate: [authGuardFn],
  },
  {
    path: 'upload',
    component: ReceiptHistoryComponent,
    canActivate: [authGuardFn],
  },
  {
    path: 'welcome',
    component: WelcomePageComponent,
  },
  {
    path: '404',
    component: NotFoundComponent
  },
  {
    path: '',
    component: HomeComponent,
    canActivate: [authGuardFn],
    pathMatch: 'full',
  },
  {// Fallback for unknown routes
    path: '**',
    redirectTo: '404'
  },
];
