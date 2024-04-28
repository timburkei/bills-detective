import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Observable } from "rxjs";
import { AuthService } from "@auth0/auth0-angular";
import {switchMap} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class UploadService {
  private apiUrl = 'http://localhost:8080/api/invoice'; // Setzen Sie Ihre API-URL hier ein

  constructor(protected auth: AuthService, private http: HttpClient) { }

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

  getUploads(userId: string): Observable<any> {
    const url = `http://localhost:8080/api/invoicesFiles/getUploadInformation/${userId}/`;
    return this.createHeader().pipe(
      switchMap(headers => this.http.get<any>(url, { headers }))
    );
  }

  uploadFile(userId: string, file: File): Observable<any> {
    const url = `${this.apiUrl}/${userId}/upload`;
    const formData = new FormData();
    formData.append('file', file);

    return new Observable((observer) => {
      this.auth.getAccessTokenSilently().subscribe(
        token => {
          // Setzen Sie den JWT-Token im Header
          const headers = new HttpHeaders({
            'Authorization': `Bearer ${token}`
          });

          // Konfiguration für den POST-Request mit dem JWT-Token im Header
          const requestOptions = {
            headers: headers,
            responseType: 'text' as 'json'
          };

          // Führen Sie den POST-Request durch
          this.http.post(url, formData, requestOptions).subscribe(
            response => {
              observer.next(response);
              observer.complete();
            },
            error => {
              observer.error(error);
              observer.complete();
            }
          );
        },
        error => {
          console.error(error);
          observer.complete();
        }
      );
    });
  }
}
