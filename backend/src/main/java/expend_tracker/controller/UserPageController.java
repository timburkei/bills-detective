package expend_tracker.controller;

import expend_tracker.dto.userPage.UserUpdateRequest;
import expend_tracker.service.AuthService;
import expend_tracker.service.RequestService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import okhttp3.*;
import okhttp3.OkHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(path = "/api/user", produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*")
public class UserPageController {

    private static final Logger log = LogManager.getLogger(UserPageController.class);

    @Value("${auth0.api.url}")
    private String auth0ApiUrl;

    @Value("${auth0.client.id}")
    private String auth0ClientId;

    @Value("${auth0.client.secret}")
    private String auth0ClientSecret;

    @Value("${auth0.audience}")
    private String auth0Audience;

    @Value("${auth0.m2m}")
    private String auth0M2M;


    private final AuthService authService;

    private final  RequestService requestService;

    public UserPageController(AuthService authService, RequestService requestService) {
        this.authService = authService;
        this.requestService = requestService;

        log.info("UserPageController initialized.");
    }

    /**
     * Returns all details of a logged-in user, based on the user ID passed.
     * This method retrieves the user information from an external API (in this case Auth0)
     * and returns it as {@link ResponseEntity<String>}. The method uses the OkHttp library
     * for the HTTP request.
     *
     * @param userId The unique user_id for which the information is to be retrieved. This ID is transmitted as a path variable in the URL.
     * @return A ResponseEntity with the user profile as a string in the body. If successful, the HTTP status code 200 (OK) is returned; if an error occurs, the corresponding HTTP status code of the external API request is returned.
     */

    @GetMapping(value = "getUserDetails/{userId}")
    @Operation(
            summary = "Returns all user details",
            description = "Retrieves all details of a logged-in user based on the user ID.",
            parameters = {
                    @Parameter(
                            name = "userId",
                            description = "The ID of the demo user",
                            example = "auth0|65630d5317b4bdb501144ab5"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved user details",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserUpdateRequest.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                       "created_at": "2023-11-26T09:18:11.400Z",
                                                       "email": "demodata@hdm-stuttgart.de",
                                                       "email_verified": false,
                                                       "identities": [
                                                         {
                                                           "connection": "Username-Password-Authentication",
                                                           "user_id": "65630d5317b4bdb501144ab5",
                                                           "provider": "auth0",
                                                           "isSocial": false
                                                         }
                                                       ],
                                                       "name": "Demo User",
                                                       "nickname": "demo",
                                                       "picture": "https://s.gravatar.com/avatar/34cc60146e9b4838a42581b46a9456a1?s=480&r=pg&d=https%3A%2F%2Fcdn.auth0.com%2Favatars%2Ftb.png",
                                                       "updated_at": "2023-11-26T14:06:04.294Z",
                                                       "user_id": "auth0|65630d5317b4bdb501144ab5",
                                                       "user_metadata": {
                                                         "persons_household": 2
                                                       }
                                                     }
                                                     """
                                    )
                            )


                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found"
                    ),
            }
    )
    public ResponseEntity<String> getUserInfo(
            @Parameter(description = "The unique ID of the user for whom information is to be retrieved")
            @PathVariable String userId) {

        log.info("Request to get user info for userId: {}", userId);

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        Request request = new Request.Builder()
                .url(auth0Audience + "users/" + userId)
                .method("GET", null)
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + getBearer())
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                log.info("Successfully retrieved user information");
                return ResponseEntity.ok(response.body().string());
            } else {
                log.error("Error while retrieving user information: " + response.code());
                return ResponseEntity.status(response.code()).build();
            }
        } catch (IOException e) {
            log.error("Error while retrieving user information: ", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Network error during the request: " + e.getMessage());
        }
    }

    /**
     * Updates the details of a logged-in user, based on the user ID passed.
     * This method updates the user information in an external API (in this case Auth0)
     * and returns it as {@link ResponseEntity<String>}. The method uses the OkHttp library
     * for the HTTP request.
     *
     * @param userId        The unique user_id for which the information is to be retrieved. This ID is transmitted as a path variable in the URL.
     * @param updateContent The updated user information as a string in JSON format. This information is transmitted as a request body.
     * @return A ResponseEntity with the updated user profile as a string in the body. If successful, the HTTP status code 200 (OK) is returned; if an error occurs, the corresponding HTTP status code of the external API request is returned.
     */
    @PatchMapping(value = "updateUserDetails/{userId}")
    @Operation(
            summary = "Updates user details",
            description = "Updates the details of a logged-in user based on the user ID.",
            parameters = {
                    @Parameter(
                            name = "userId",
                            description = "The ID of the demo user",
                            example = "auth0|65630d5317b4bdb501144ab5"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserUpdateRequest.class),
                            examples = @ExampleObject(
                                    name = "The parameters listed above are all parameters that may be updated. The documentation can you find here https://auth0.com/docs/api/management/v2/users/get-authentication-methods",
                                    summary = "Example of updating a user",
                                    value = """
                                            {
                                                "email": "demodata@hdm-stuttgart.de",
                                                "name": "Demo User",
                                                "nickname": "demo",
                                                "picture": "https://s.gravatar.com/avatar/34cc60146e9b4838a42581b46a9456a1?s=480&r=pg&d=https%3A%2F%2Fcdn.auth0.com%2Favatars%2Ftb.png",
                                                "user_metadata": {
                                                    "persons_household": 2
                                                }
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated user details",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserUpdateRequest.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "created_at": "2023-11-26T09:18:11.400Z",
                                                      "email": "demodata@hdm-stuttgart.de",
                                                      "email_verified": false,
                                                      "identities": [
                                                        {
                                                          "connection": "Username-Password-Authentication",
                                                          "user_id": "65630d5317b4bdb501144ab5",
                                                          "provider": "auth0",
                                                          "isSocial": false
                                                        }
                                                      ],
                                                      "name": "Demo User",
                                                      "nickname": "demo",
                                                      "picture": "https://s.gravatar.com/avatar/34cc60146e9b4838a42581b46a9456a1?s=480&r=pg&d=https%3A%2F%2Fcdn.auth0.com%2Favatars%2Ftb.png",
                                                      "updated_at": "2023-11-26T13:04:44.741Z",
                                                      "user_id": "auth0|65630d5317b4bdb501144ab5",
                                                      "user_metadata": {
                                                        "persons_household": 2
                                                      }
                                                    }"""
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<String> updateUser(@PathVariable String userId,
                                             @RequestBody String updateContent) {

        log.info("Request to update user info for userId: {}", userId);

        requestService.isUserIdEmpty(userId);
        requestService.isContentEmpty(updateContent);

        return authService.handlePatchRequestToAuthServer(userId, updateContent, getBearer());
    }


    /**
     * Returns the access token for the Auth0 API.
     * This method retrieves the access token from the Auth0 API and returns it as a string.
     * The method uses the Unirest library for the HTTP request.
     *
     * @return The access token as a string.
     */
    private String getBearer() {
        HttpResponse<String> response = Unirest.post(auth0M2M)
                .header("content-type", "application/json")
                .body("{\"client_id\":\"" + auth0ClientId + "\",\"client_secret\":\"" + auth0ClientSecret + "\",\"audience\":\"" + auth0Audience + "\",\"grant_type\":\"client_credentials\"}")
                .asString();

        JSONObject jsonResponse = new JSONObject(response.getBody());
        String accessToken = jsonResponse.getString("access_token");
        return accessToken;
    }
}
