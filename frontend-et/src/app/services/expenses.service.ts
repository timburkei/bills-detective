import { Injectable } from '@angular/core';
import {AuthService} from "@auth0/auth0-angular";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {switchMap} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class ExpensesService {
  private apiUrl = 'http://localhost:8080/api/expenditure-page';

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
   * Retrieve expenses for a specific product for a user, optionally filtered by date.
   *
   * @param userId - Current user
   * @param productId - Product
   */
  getProductExpenses(userId: string, productId: number): Observable<any> {
    const url = `${this.apiUrl}/${userId}/products/${productId}/expenses`;
    return this.createHeader().pipe(
      switchMap(headers => this.http.get<any>(url, { headers }))
    );
  }

  /**
   * Retrieve all invoice items for a specific user and invoice.
   *
   * @param userId - Current user
   * @param invoiceId - Receipt
   */
  getInvoiceItems(userId: string, invoiceId: number): Observable<any> {
    const url = `${this.apiUrl}/${userId}/invoices/${invoiceId}/items/`;
    return this.createHeader().pipe(
      switchMap(headers => this.http.get<any>(url, { headers }))
    );
  }

  /**
   * Retrieve all invoices for a specific user, optionally filtered by date.
   *
   * @param userId - Current user
   * @param chainsId - Chain to filter invoices
   * @param categoryId - category to filter invoices
   */
  getInvoices(userId: string, chainsId: string | undefined, categoryId: string | undefined): Observable<any> {
    let url: string;
    if(!chainsId && !categoryId) { //invoices/?chainIds=1&tagIds=1
      url = `${this.apiUrl}/${userId}/invoices/`;
    } else if(!chainsId && categoryId) {
      url = `${this.apiUrl}/${userId}/invoices/?tagIds=${categoryId}`;
    }else if(chainsId && !categoryId) {
      url = `${this.apiUrl}/${userId}/invoices/?chainIds=${chainsId}`;
    } else {
      url = `${this.apiUrl}/${userId}/invoices/?chainIds=${chainsId}&tagIds=${categoryId}`;
    }
    return this.createHeader().pipe(
      switchMap(headers => this.http.get<any>(url, { headers }))
    );
  }

  /**
   * Retrieve all expenses, chosen by the user to generate a customized chart.
   *
   * @param userId - Current user.
   * @param chainIds - Chains for generating chart.
   * @param tagIds - Categories for generating chart.
   */
  getChainTagExpenses(userId: string, chainIds: string[], tagIds: string[]): Observable<any> {
    const chainIdsQueryString = chainIds.map(id => `chainIds=${id}`).join('&');
    const tagIdsQueryString = tagIds.map(id => `tagIds=${id}`).join('&');

    const url = `${this.apiUrl}/${userId}/chain-tag-expenses?${chainIdsQueryString}&${tagIdsQueryString}`;

    return this.createHeader().pipe(
      switchMap(headers => this.http.get<any>(url, { headers }))
    );
  }
}
