package expend_tracker.controller;

import expend_tracker.dto.invoice.InvoiceData;
import expend_tracker.service.invoicePage.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@RestController
@RequestMapping(path = "/api/invoice", produces = MediaType.APPLICATION_JSON_VALUE)
// For simplicity of this sample, allow all origins. Real applications should configure CORS for their use case.
@CrossOrigin(origins = "*")
public class InvoiceController {

    @Value("${ai.api.key}")
    private String expectedApiKey;

    private static final Logger log = LogManager.getLogger(InvoiceController.class);
    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceController(
            InvoiceService invoiceService
    ) {
        this.invoiceService = invoiceService;

        log.info("InvoiceController initialized.");
    }

    /**
     * Uploads an invoice file.
     *
     * @param userId The ID of the user uploading the file.
     * @param file   The invoice file to be uploaded.
     * @return ResponseEntity with the upload status.
     */
    @Operation(summary = "Upload Invoice File", description = "Uploads an invoice file for processing.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully uploaded the invoice file",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE)),
            @ApiResponse(responseCode = "400", description = "Invalid request if the file is empty"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error if file upload fails")
    })
    @PostMapping("/{userId}/upload")
    public ResponseEntity<String> uploadImage(
            @PathVariable String userId,
            @RequestParam("file") MultipartFile file) {

        log.info("Received request to upload invoice file for user: {}", userId);

        if (file.isEmpty()) {
            log.warn("Attempt to upload an empty file by user: {}", userId);
            return ResponseEntity.badRequest().body("File is empty");
        }

        try {
            String responseMessage = invoiceService.uploadInvoiceFile(file, userId);
            log.info("Invoice file uploaded successfully for user: {}", userId);
            return ResponseEntity.ok(responseMessage);
        } catch (IllegalArgumentException e) {
            log.error("Error uploading the file", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            log.error("Error uploading the file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading the file: " + e.getMessage());
        }
    }

    /**
     * Processes invoice data.
     *
     * @param invoiceData The data of the invoice to be processed.
     * @return ResponseEntity with the processing status.
     */
    @Operation(summary = "Process Invoice Data", description = "Processes the provided invoice data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully processed the invoice data",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE)),
            @ApiResponse(responseCode = "500", description = "Internal Server Error if processing fails")
    })
    @PostMapping("/setInvoiceData")
    public ResponseEntity<String> createInvoice(@RequestBody InvoiceData invoiceData,
                                                @RequestHeader HttpHeaders headers) {
        if (!validateApiKey(headers)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Invalid API Key");
        }
        
        log.info("Received request to process invoice data for user: {}", invoiceData.getUserId());

        try {
            invoiceService.processInvoiceData(invoiceData);
            log.info("Invoice data processed successfully for user: {}", invoiceData.getUserId());
            return ResponseEntity.status(HttpStatus.CREATED).body("Invoice data successfully processed.");
        } catch (Exception e) {
            log.error("Internal server error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error: " + e.getMessage());
        }
    }
    
    private boolean validateApiKey(HttpHeaders headers) {
        String apiKey = headers.getFirst("X-API-Key");
        return apiKey != null && apiKey.equals(expectedApiKey);
    }

}