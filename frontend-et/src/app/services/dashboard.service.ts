import { Injectable } from '@angular/core';
import {AuthService} from "@auth0/auth0-angular";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {switchMap} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private apiUrl = 'http://localhost:8080/api/dashboard-page';

  constructor(public auth: AuthService, private http: HttpClient) { }

  private createHeader(): Observable<HttpHeaders> {
    return new Observable((observer) => {
      this.auth.getAccessTokenSilently().subscribe(
        token => {
          const headers = new HttpHeaders({
            'Authorization': `Bearer ${token}`
          });
          observer.next(headers);  // Send headers to the observer
          observer.complete();  // Complete the observable
        },
        error => {
          observer.error(error);  // If there's an error, send it to the observer
        }
      );
    });
  }

  /**
   * Retrieve shopping pattern for a specified user.
   *
   * @param userId - Current user
   */
  getShoppingPattern(userId: string): Observable<any> {
    const url = `${this.apiUrl}/user-shopping-pattern/${userId}`;
    return this.createHeader().pipe(
      switchMap(headers => this.http.get<any>(url, { headers }))
    );
  }

  /**
   * Retrieve tag-based expenses for a specified user.
   *
   * @param userId - Current user
   */
  getTagExpenses(userId: string): Observable<any> {
    const url = `${this.apiUrl}/tag-expenditure/${userId}`;
    return this.createHeader().pipe(
      switchMap(headers => this.http.get<any>(url, { headers }))
    );
  }

  /**
   * Retrieve monthly expenses for a specified user.
   *
   * @param userId - Current user
   */
  getMonthlyExpenses(userId: string): Observable<any> {
    const url = `${this.apiUrl}/monthly-expenditure/${userId}`;
    return this.createHeader().pipe(
      switchMap(headers => this.http.get<any>(url, { headers }))
    );
  }

  /**
   * Retrieve chain expenses for a specified user.
   *
   * @param userId - Current user.
   */
  getChainExpenses(userId: string): Observable<any> {
    const url = `${this.apiUrl}/chain-expenditure/${userId}`;
    return this.createHeader().pipe(
      switchMap(headers => this.http.get<any>(url, { headers }))
    );
  }
}
