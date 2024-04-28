import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {AuthService} from "@auth0/auth0-angular";
import {switchMap} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class FilterService {
  private apiUrl = 'http://localhost:8080/api/user-service';

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
   * Retrieve all chains linked to the invoices for a specific user.
   *
   * @param userId - Current user
   */
  getChains(userId: string): Observable<any> {
    const url = `${this.apiUrl}/getChainFor/${userId}`;
    return this.createHeader().pipe(
      switchMap(headers => this.http.get<any>(url, { headers }))
    );
  }

  /**
   * Retrieve all categories linked to the invoices for a specific user.
   *
   * @param userId - Current user
   */
  getCategories(userId: string): Observable<any> {
    const url = `${this.apiUrl}/getTagsFor/${userId}`;
    return this.createHeader().pipe(
      switchMap(headers => this.http.get<any>(url, { headers }))
    );
  }
}

