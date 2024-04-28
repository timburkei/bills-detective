package expend_tracker.dto.userPage;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * DTO for user update request.
 */
@Data
public class UserUpdateRequest {

    /**
     * The user's email address.
     */
    private String email;

    /**
     * The user's name.
     */
    private String name;

    /**
     * The user's nickname.
     */
    private String nickname;

    /**
     * The user's picture.
     */
    private String picture;

    /**
     * The user's metadata.
     */
    @JsonProperty("user_metadata")
    private UserMetadata userMetadata;

    /**
     * The user's app metadata.
     */
    @Data
    public static class UserMetadata {
        @JsonProperty("persons_household")
        private Integer personsHousehold;
    }
}

