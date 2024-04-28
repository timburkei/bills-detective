package expend_tracker.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import expend_tracker.dto.userService.ChainDto;
import expend_tracker.dto.userService.TagDto;
import expend_tracker.service.userService.ChainService;
import expend_tracker.service.userService.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;

@RestController
@RequestMapping(path = "/api/user-service", produces = MediaType.APPLICATION_JSON_VALUE)
// For simplicity of this sample, allow all origins. Real applications should
// configure CORS for their use case.
public class UserServiceController {

    private static final Logger log = LogManager.getLogger(UserServiceController.class);

    private final ChainService chainService;

    private final TagService tagService;

    @Autowired
    public UserServiceController(ChainService chainService,
                                TagService tagService) {
        this.chainService = chainService;
        this.tagService = tagService;
    }

    /**
     * Retrieves all chain data linked to the invoices of the specified user.
     *
     * @param userId The unique identifier of the user whose chain data is to be
     *               retrieved.
     * @return A {@link ResponseEntity} containing a set of {@link ChainDto}
     *         representing the chains linked to the user's invoices.
     */
    @Operation(summary = "Get chains by user", description = "Retrieve all chains linked to the invoices of a specific user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the chains", content = @Content(schema = @Schema(implementation = ChainDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred while processing the request")
    })
    @GetMapping("/getChainFor/{userId}")
    public ResponseEntity<Set<ChainDto>> getChainsByUserId(
            @Parameter(description = "The ID of the user to retrieve chains for", required = true, example = "auth0|65630d5317b4bdb501144ab5") @PathVariable String userId,
            @Parameter(description = "The start date of the range within which to retrieve the chains. This parameter is optional.", example = "01.01.2024") @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate startDate,
            @Parameter(description = "The end date of the range within which to retrieve the chains. This parameter is optional.", example = "31.12.2024") @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate endDate) {
        try {
            Set<ChainDto> chains = chainService.getChainsForUser(userId, startDate, endDate);
            return ResponseEntity.ok(chains);
        } catch (Exception e) {
            log.error("Failed to get chains for user: {}", userId, e);
            return ResponseEntity.internalServerError().build();
        }
    }


    /**
     * Retrieves all tags linked to the invoices of the specified user within a given date range.
     *
     * @param userId    The unique identifier of the user whose tags are to be retrieved.
     * @param startDate The start date of the range within which to retrieve the tags. This parameter is optional.
     * @param endDate   The end date of the range within which to retrieve the tags. This parameter is optional.
     * @return A {@link ResponseEntity} containing a list of {@link TagDto} representing the tags linked to the user's invoices.
     * @throws Exception If an error occurs while processing the request.
     */
    @Operation(summary = "Get tags by user", description = "Retrieve all tags linked to the invoices of a specific user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the tags"),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred while processing the request")
    })
    @GetMapping("/getTagsFor/{userId}")
    public ResponseEntity<List<TagDto>> getTagsByUserId(
            @Parameter(description = "The ID of the user to retrieve chains for", required = true, example = "auth0|65630d5317b4bdb501144ab5") @PathVariable String userId,
            @Parameter(description = "The start date of the range within which to retrieve the tag. This parameter is optional.", example = "01.01.2024") @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate startDate,
            @Parameter(description = "The end date of the range within which to retrieve the chains. This parameter is optional.", example = "31.12.2024") @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate endDate) {
        try {
            List<TagDto> tags = tagService.getTagsForUser(userId, startDate, endDate);
            return ResponseEntity.ok(tags);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


}
