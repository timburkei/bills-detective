import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ProfileService {

  constructor(private http: HttpClient) { }

  userId: string = "auth0|65630d5317b4bdb501144ab5";
  encodedUserId: string = encodeURIComponent(this.userId);
  urlUserDetails: string = `http://localhost:8080/api/user/getUserDetails/${this.encodedUserId}`;
  urlUpdateDetails: string = `http://localhost:8080/api/user/updateUserDetails/${this.encodedUserId}`;

  /**
   * Retrieves all details of a logged-in user based on the user ID.
   */
  getUserDetails(): Observable<any> {
    return this.http.get<any>(this.urlUserDetails);
  }

  /**
   * Updates the profile image of a logged-in user based on the user ID.
   * @param image - ID of the selected image
   */
  updateUserDetails(image: number): Observable<any> {
    let userMetadata: { image: number } = {
      image: image
    };
    // Assuming userMetadata is an object containing the desired updates
    return this.http.patch(this.urlUpdateDetails, { user_metadata: userMetadata });
  }

  /**
   * Sets a default image for every newly-registered user based on the user ID.
   */
  setDefaultImage(): Observable<any> {
    let userMetadata = {
      image: 1
    }
    // Assuming userMetadata is an object containing the desired updates
    return this.http.patch(this.urlUpdateDetails, { user_metadata: userMetadata });
  }
}
