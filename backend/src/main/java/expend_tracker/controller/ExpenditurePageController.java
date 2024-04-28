package expend_tracker.controller;

import expend_tracker.dto.expensesPage.ChainExpenseDto;
import expend_tracker.dto.expensesPage.InvoiceExpensesDto;
import expend_tracker.dto.expensesPage.InvoiceItemExpensesDto;
import expend_tracker.dto.expensesPage.ProductExpensesDto;
import expend_tracker.service.expensesPage.ChainTagExpenseService;
import expend_tracker.service.expensesPage.InvoiceExpensesService;
import expend_tracker.service.expensesPage.InvoiceItemExpensesService;
import expend_tracker.service.expensesPage.ProductExpenseService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


/**
 * Controller for handling requests related to the expenditure page.
 * Provides endpoints for retrieving invoices, invoice items, and product expenses.
 */
@RestController
@RequestMapping(path = "/api/expenditure-page", produces = MediaType.APPLICATION_JSON_VALUE)
// For simplicity of this sample, allow all origins. Real applications should configure CORS for their use case.
@CrossOrigin(origins = "*")
public class ExpenditurePageController {


    private static final Logger log = LogManager.getLogger(ExpenditurePageController.class);
    private final InvoiceExpensesService invoiceExpensesService;
    private final InvoiceItemExpensesService invoiceItemExpensesService;
    private final ProductExpenseService productExpenseService;

    private final ChainTagExpenseService chainTagExpenseService;


    @Autowired
    public ExpenditurePageController(InvoiceExpensesService invoiceExpensesService,
                                     InvoiceItemExpensesService invoiceItemExpensesService,
                                     ProductExpenseService productExpenseService,
                                     ChainTagExpenseService chainTagExpenseService) {
        this.invoiceExpensesService = invoiceExpensesService;
        this.invoiceItemExpensesService = invoiceItemExpensesService;
        this.productExpenseService = productExpenseService;
        this.chainTagExpenseService = chainTagExpenseService;
    

        log.info("ExpenditurePageController initialized.");
    }

    /**
     * Retrieves a list of invoices for a specified user within a given date range.
     *
     * @param userId    The ID of the user.
     * @param startDate Optional start date for filtering invoices (format dd.MM.yyyy).
     * @param endDate   Optional end date for filtering invoices (format dd.MM.yyyy).
     * @param chainIds  Optional list of chain IDs for filtering invoices.
     * @param tagIds Optional list of product IDs for filtering invoices.
     * @return List of {@link InvoiceExpensesDto} for the user.
     */
    @Operation(summary = "Get invoices by user", description = "Retrieve all invoices for a specific user, optionally filtered by date.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of invoices"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping(value = "/{userId}/invoices/")
    public List<InvoiceExpensesDto> getInvoicesByUser(
            @Parameter(description = "User ID", example = "auth0|65630d5317b4bdb501144ab5") @PathVariable String userId,
            @Parameter(description = "Start date for filtering (format dd.MM.yyyy)", example = "01.01.2024") @RequestParam @DateTimeFormat(pattern = "dd.MM.yyyy") Optional<LocalDate> startDate,
            @Parameter(description = "End date for filtering (format dd.MM.yyyy)", example = "31.01.2024") @RequestParam @DateTimeFormat(pattern = "dd.MM.yyyy") Optional<LocalDate> endDate,
            @Parameter(description = "Chain IDs for filtering", example = "[1, 2]") @RequestParam(required = false) List<Long> chainIds,
            @Parameter(description = "Tag IDs for filtering", example = "[1, 2]") @RequestParam(required = false) List<Long> tagIds) {

        log.info("getInvoicesByUser called with userId: " + userId + ", startDate: " + startDate + ", endDate: " + endDate + ", chainIds: " + chainIds + ", tagIds: " + tagIds);
        return invoiceExpensesService.getInvoicesByUser(userId, startDate.orElse(null), endDate.orElse(null), chainIds, tagIds);
    }

    /**
     * Retrieves a list of invoice items for a specific user and invoice.
     *
     * @param userId    The ID of the user.
     * @param invoiceId The ID of the invoice.
     * @return List of {@link InvoiceItemExpensesDto} for the specified invoice.
     */
    @Operation(summary = "Get invoice items by user and invoice", description = "Retrieve all invoice items for a specific user and invoice.")
    @GetMapping(value = "/{userId}/invoices/{invoiceId}/items/")
    public List<InvoiceItemExpensesDto> getInvoiceItemsByUserAndInvoice(
            @Parameter(description = "User ID", example = "auth0|65630d5317b4bdb501144ab5") @PathVariable String userId,
            @Parameter(description = "Invoice ID", example = "1") @PathVariable Long invoiceId) {

        log.info("getInvoiceItemsByUserAndInvoice called with userId: " + userId + ", invoiceId: " + invoiceId);
        return invoiceItemExpensesService.getInvoiceItemsByUserAndInvoice(userId, invoiceId);
    }

    /**
     * Retrieves the expenses for a specific product for a user within a given date range.
     *
     * @param userId    The ID of the user.
     * @param productId The ID of the product.
     * @param startDate Optional start date for filtering expenses (format dd.MM.yyyy).
     * @param endDate   Optional end date for filtering expenses (format dd.MM.yyyy).
     * @return {@link ProductExpensesDto} containing the expense details for the product.
     */
    @Operation(summary = "Get product expenses", description = "Retrieve expenses for a specific product for a user, optionally filtered by date.")
    @GetMapping(value = "/{userId}/products/{productId}/expenses")
    public ProductExpensesDto getProductExpenses(
            @Parameter(description = "User ID", example = "auth0|65630d5317b4bdb501144ab5") @PathVariable String userId,
            @Parameter(description = "Product ID", example = "1") @PathVariable Long productId,
            @Parameter(description = "Start date for filtering (format dd.MM.yyyy)", example = "01.01.2024") @RequestParam @DateTimeFormat(pattern = "dd.MM.yyyy") Optional<LocalDate> startDate,
            @Parameter(description = "End date for filtering (format dd.MM.yyyy)", example = "31.01.2024") @RequestParam @DateTimeFormat(pattern = "dd.MM.yyyy") Optional<LocalDate> endDate) {

        log.info("getProductExpenses called with userId: " + userId + ", productId: " + productId + ", startDate: " + startDate + ", endDate: " + endDate);
        return productExpenseService.calculateExpenses(productId, userId, startDate.orElse(null), endDate.orElse(null));
    }

    /**
     * Retrieves a list of chain tag expenses for a specified user within a given date range.
     *
     * @param userId    The ID of the user.
     * @param chainIds  List of chain IDs for filtering expenses.
     * @param tagIds    List of tag IDs for filtering expenses.
     * @param startDate Optional start date for filtering expenses (format dd.MM.yyyy).
     * @param endDate   Optional end date for filtering expenses (format dd.MM.yyyy).
     * @return List of {@link ChainExpenseDto} for the user.
     */
    @Operation(summary = "Get chain tag expenses by user", description = "Retrieve all chain tag expenses for a specific user, optionally filtered by date.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of chain tag expenses"),
            @ApiResponse(responseCode = "400", description = "Bad request, at least one of the parameters 'chainIds' or 'tagIds' must be provided"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
   /* @GetMapping(value = "/{userId}/chain-tag-expenses")
    public List<ChainExpenseDto> getChainTagExpensesByUser(
            @PathVariable String userId,
            @RequestParam Optional<List<Long>> chainIds,
            @RequestParam Optional<List<Long>> tagIds,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Optional<LocalDate> startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Optional<LocalDate> endDate) {

        log.info("getChainTagExpensesByUser called with userId: " + userId + ", chainIds: " + chainIds + ", tagIds: " + tagIds + ", startDate: " + startDate + ", endDate: " + endDate);

        if ((chainIds == null || chainIds.isEmpty()) && (tagIds == null || tagIds.isEmpty())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one of the parameters 'chainIds' or 'tagIds' must be provided.");
        }

        return chainTagExpenseService.calculateChainTagExpenses(userId, chainIds.orElse(null), tagIds.orElse(null), startDate, endDate);
    }*/
    @GetMapping(value = "/{userId}/chain-tag-expenses")
    public List<ChainExpenseDto> getChainTagExpensesByUser(
            @PathVariable String userId,
            @RequestParam Optional<List<Long>> chainIds,
            @RequestParam Optional<List<Long>> tagIds,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Optional<LocalDate> startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") Optional<LocalDate> endDate) {

        log.info("getChainTagExpensesByUser called with userId: " + userId + ", chainIds: " + chainIds + ", tagIds: " + tagIds + ", startDate: " + startDate + ", endDate: " + endDate);

        return chainTagExpenseService.calculateChainTagExpenses(userId, chainIds.orElse(null), tagIds.orElse(null), startDate, endDate);
    }

}