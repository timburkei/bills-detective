package expend_tracker.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Service for handling request validations.
 */
@Service
public class RequestService {
    private static final Logger log = LogManager.getLogger(RequestService.class);

    /**
     * Checks if the user ID is empty.
     *
     * @param userId The user ID to check.
     * @return ResponseEntity indicating the result of the check.
     */
    public ResponseEntity<String> isUserIdEmpty(String userId) {
        try {
            if (userId == null || userId.trim().isEmpty()) {
                log.warn("User ID is empty or null");
                return ResponseEntity.badRequest().body("Invalid user ID");
            }
            return ResponseEntity.ok("User ID is valid");
        } catch (Exception e) {
            log.error("Error checking user ID: ", e);
            return ResponseEntity.internalServerError().body("Error checking user ID");
        }
    }

    /**
     * Checks if the content is empty.
     *
     * @param content The content to check.
     * @return ResponseEntity indicating the result of the check.
     */
    public ResponseEntity<String> isContentEmpty(String content) {
        try {
            if (content == null || content.trim().isEmpty()) {
                log.warn("Update content is empty or null");
                return ResponseEntity.badRequest().body("Invalid update content");
            }
            return ResponseEntity.ok("Content is valid");
        } catch (Exception e) {
            log.error("Error checking content: ", e);
            return ResponseEntity.internalServerError().body("Error checking content");
        }
    }
}