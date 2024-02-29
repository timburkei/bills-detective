package expend_tracker.controller;

import expend_tracker.dto.dashboardPage.ChainExpenseDto;
import expend_tracker.dto.dashboardPage.MonthlyExpenseDto;
import expend_tracker.dto.dashboardPage.TagExpenseDto;
import expend_tracker.dto.dashboardPage.UserShoppingPatternDto;
import expend_tracker.service.dashboardPage.ChainExpenseService;
import expend_tracker.service.dashboardPage.MonthlyExpenseService;
import expend_tracker.service.dashboardPage.TagExpenseService;
import expend_tracker.service.dashboardPage.UserShoppingPatternService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for handling requests related to the dashboard page.
 * Provides endpoints for retrieving monthly, chain, and tag based expenses.
 */
@RestController
@RequestMapping(path="/api/dashboard-page", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*")
public class DashboardPageController {

    private static final Logger log = LogManager.getLogger(DashboardPageController.class);

    private final MonthlyExpenseService monthlyExpenseService;
    private final ChainExpenseService chainExpenseService;
    private final TagExpenseService tagExpenseService;

    private final UserShoppingPatternService userShoppingPatternService;

    @Autowired
    public DashboardPageController(MonthlyExpenseService monthlyExpenseService,
                                   ChainExpenseService chainExpenseService,
                                   TagExpenseService tagExpenseService,
                                   UserShoppingPatternService userShoppingPatternService) {
        this.monthlyExpenseService = monthlyExpenseService;
        this.chainExpenseService = chainExpenseService;
        this.tagExpenseService = tagExpenseService;
        this.userShoppingPatternService = userShoppingPatternService;
        log.info("DashboardPageController initialized.");
    }

    /**
     * Retrieves a list of monthly expenses for a given user.
     *
     * @param userId    The ID of the user.
     * @param startDate The start date for filtering expenses (format dd.MM.yyyy).
     * @param endDate   The end date for filtering expenses (format dd.MM.yyyy).
     * @return List of {@link MonthlyExpenseDto}.
     */
    @Operation(summary = "Get monthly expenses", description = "Retrieve monthly expenses for a specified user.")
    @GetMapping("/monthly-expenditure/{userId}")
    public List<MonthlyExpenseDto> getMonthlyExpenses(
            @Parameter(description = "User ID", example = "auth0|65630d5317b4bdb501144ab5") @PathVariable String userId,
            @Parameter(description = "Start date for filtering (format dd.MM.yyyy)", example = "01.01.2024") @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate startDate,
            @Parameter(description = "End date for filtering (format dd.MM.yyyy)", example = "31.01.2024") @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate endDate) {

        log.info("getMonthlyExpenses called with userId: " + userId + ", startDate: " + startDate + ", endDate: " + endDate);
        return monthlyExpenseService.calculateMonthlyExpenses(userId, startDate, endDate);
    }


    /**
     * Retrieves a list of chain expenses for a given user.
     *
     * @param userId    The ID of the user.
     * @param startDate The start date for filtering expenses (format dd.MM.yyyy).
     * @param endDate   The end date for filtering expenses (format dd.MM.yyyy).
     * @return List of {@link ChainExpenseDto}.
     */
    @Operation(summary = "Get chain expenses", description = "Retrieve chain expenses for a specified user.")
    @GetMapping("/chain-expenditure/{userId}")
    public List<ChainExpenseDto> getChainExpenses(
            @Parameter(description = "User ID", example = "auth0|65630d5317b4bdb501144ab5") @PathVariable String userId,
            @Parameter(description = "Start date for filtering (format dd.MM.yyyy)", example = "01.01.2024") @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate startDate,
            @Parameter(description = "End date for filtering (format dd.MM.yyyy)", example = "31.01.2024") @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate endDate) {

        log.info("getChainExpenses called with userId: " + userId + ", startDate: " + startDate + ", endDate: " + endDate);
        return chainExpenseService.calculateExpenses(userId, startDate, endDate);
    }

    /**
     * Retrieves a list of tag-based expenses for a given user.
     *
     * @param userId    The ID of the user.
     * @param startDate The start date for filtering expenses (format dd.MM.yyyy).
     * @param endDate   The end date for filtering expenses (format dd.MM.yyyy).
     * @return List of {@link TagExpenseDto}.
     */
    @Operation(summary = "Get tag expenses", description = "Retrieve tag-based expenses for a specified user.")
    @GetMapping("/tag-expenditure/{userId}")
    public List<TagExpenseDto> getTagExpenses(
            @Parameter(description = "User ID", example = "auth0|65630d5317b4bdb501144ab5") @PathVariable String userId,
            @Parameter(description = "Start date for filtering (format dd.MM.yyyy)", example = "01.01.2024") @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate startDate,
            @Parameter(description = "End date for filtering (format dd.MM.yyyy)", example = "31.01.2024") @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate endDate) {

        log.info("getTagExpenses called with userId: " + userId + ", startDate: " + startDate + ", endDate: " + endDate);
        return tagExpenseService.calculateTagExpenses(userId, startDate, endDate);
    }

    /**
     * Retrieves the shopping pattern for a given user.
     *
     * @param userId    The ID of the user.
     * @param startDate The start date for filtering expenses (format dd.MM.yyyy).
     * @param endDate   The end date for filtering expenses (format dd.MM.yyyy).
     * @return {@link UserShoppingPatternDto}.
     */
    @Operation(summary = "Get user shopping pattern", description = "Retrieve shopping pattern for a specified user.")
    @GetMapping("/user-shopping-pattern/{userId}")
    public UserShoppingPatternDto getUserShoppingPattern(
            @Parameter(description = "User ID", example = "auth0|65630d5317b4bdb501144ab5") @PathVariable String userId,
            @Parameter(description = "Start date for filtering (format dd.MM.yyyy)", example = "01.01.2024") @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate startDate,
            @Parameter(description = "End date for filtering (format dd.MM.yyyy)", example = "31.01.2024") @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate endDate) {

        log.info("getUserShoppingPattern called with userId: " + userId + ", startDate: " + startDate + ", endDate: " + endDate);
        return userShoppingPatternService.calculateUserShoppingPattern(userId, startDate, endDate);
    }



}
