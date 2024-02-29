package expend_tracker.service;

import expend_tracker.exception.CustomNetworkException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class AuthService {

    @Value("${auth0.audience}")
    private String auth0Audience;
    private static final Logger log = LogManager.getLogger(AuthService.class);
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json");
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_BACKOFF_MS = 1000;

    /**
     * Handles a PATCH request to the Auth0 server.
     *
     * @param userId        The user ID to be updated.
     * @param updateContent The content to update.
     * @param bearerToken   The bearer token for authentication.
     * @return ResponseEntity with the response or error details.
     */
    public ResponseEntity<String> handlePatchRequestToAuthServer(String userId, String updateContent, String bearerToken) {
        OkHttpClient client = new OkHttpClient();
        okhttp3.RequestBody body = okhttp3.RequestBody.create(updateContent, JSON_MEDIA_TYPE);
        Request request = buildPatchRequest(userId, body, bearerToken);

        try {
            log.info("Sending PATCH request to Auth0 server");
            return executeRequestWithRetry(client, request);
        } catch (CustomNetworkException e) {
            log.error("Network error while handling patch request: ", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
        }
    }

    private Request buildPatchRequest(String userId, okhttp3.RequestBody body, String bearerToken) {
        return new Request.Builder()
                .url(auth0Audience + "users/" + userId)
                .patch(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + bearerToken)
                .build();
    }

    private ResponseEntity<String> executeRequestWithRetry(OkHttpClient client, Request request) throws CustomNetworkException {
        for (int retries = 0; retries < MAX_RETRIES; retries++) {
            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body() != null ? response.body().string() : null;
                return ResponseEntity.ok(responseBody);
            } catch (IOException e) {
                handleRetry(retries, e);
            }
        }
        throw new CustomNetworkException("Network error after maximum retries", HttpStatus.SERVICE_UNAVAILABLE);
    }

    private void handleRetry(int retries, IOException e) throws CustomNetworkException {
        if (retries >= MAX_RETRIES - 1) {
            throw new CustomNetworkException("Network error: " + e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        }
        log.info("Retry request, attempt: {}", retries + 1);
        try {
            Thread.sleep(RETRY_BACKOFF_MS * (retries + 1));
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            log.error("Error while waiting between request attempts: ", ie);
        }
    }
}