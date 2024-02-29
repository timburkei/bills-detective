package expend_tracker.controller;

import expend_tracker.dto.invoiceFilePage.InvoiceFileDto;
import expend_tracker.service.invoiceFilePage.InvoiceFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;


import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/invoicesFiles")
@CrossOrigin(origins = "*")
public class InvoiceFileController {

    private static final Logger log = LogManager.getLogger(InvoiceFileController.class);

    private final InvoiceFileService invoiceFileService;

    public InvoiceFileController(InvoiceFileService invoiceFileService) {
        this.invoiceFileService = invoiceFileService;

        log.info("InvoiceFileController initialized.");
    }

    @Operation(summary = "Get Invoice Files Upload Information", description = "Get Invoice Files Upload Information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved invoice files",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = InvoiceFileDto.class)) }),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("getUploadInformation/{userId}/")
    public List<InvoiceFileDto> getInvoiceFiles(
            @Parameter(description = "The ID of the user", example = "auth0|65630d5317b4bdb501144ab5") @PathVariable String userId,
            @Parameter(description = "Start date of the range", example = "01.01.2024")
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate startDate,
            @Parameter(description = "End date of the range", example = "31.01.2024")
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate endDate) {

        log.info("getInvoiceFiles called with userId: " + userId + ", startDate: " + startDate + ", endDate: " + endDate);
        return invoiceFileService.getInvoiceFiles(userId, startDate, endDate);
    }
}