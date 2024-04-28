package expend_tracker.service.dashboardPage;

import expend_tracker.dto.dashboardPage.MonthlyExpenseDto;
import expend_tracker.model.InvoiceItem;
import expend_tracker.repositories.InvoiceItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class for calculating monthly expenses.
 * This class provides functionalities to compute and retrieve monthly expense data for a given user
 * over a specified date range.
 */
@Service
public class MonthlyExpenseService {

    private static final Logger log = LoggerFactory.getLogger(MonthlyExpenseService.class);
    private final InvoiceItemRepository invoiceItemRepository;

    @Autowired
    public MonthlyExpenseService(InvoiceItemRepository invoiceItemRepository) {
        this.invoiceItemRepository = invoiceItemRepository;
    }

    /**
     * Calculates the monthly expenses for a given user between the specified start and end dates.
     * It aggregates the expenses per month and returns a list of MonthlyExpenseDto objects representing
     * the monthly totals. If an error occurs, an empty list is returned.
     *
     * @param userId    the ID of the user for whom the expenses are calculated
     * @param startDate the starting date of the period for calculating expenses
     * @param endDate   the ending date of the period for calculating expenses
     * @return a list of MonthlyExpenseDto objects representing monthly expenses, or an empty list in case of an error
     */
    public List<MonthlyExpenseDto> calculateMonthlyExpenses(String userId, LocalDate startDate, LocalDate endDate) {
        try {
            LocalDate start = determineStartDate(startDate);
            LocalDate end = determineEndDate(endDate);
            log.info("Calculating monthly expenses for user: {} from {} to {}", userId, start, end);

            List<InvoiceItem> invoiceItems = retrieveInvoiceItems(userId, start, end);
            Map<YearMonth, Double> monthlyTotals = calculateMonthlyTotals(invoiceItems);

            List<MonthlyExpenseDto> monthlyExpenses = convertToMonthlyExpenseDtos(monthlyTotals);
            log.info("Monthly expenses calculated successfully for user: {}", userId);
            return monthlyExpenses;
        } catch (Exception e) {
            log.error("Error calculating monthly expenses for user: {}", userId, e);
            return Collections.emptyList();
        }
    }

    /**
     * Determines the start date for expense calculation.
     * If a start date is provided, it is used; otherwise, the first day of the current month is used.
     *
     * @param startDate the provided start date
     * @return the determined start date
     */
    private LocalDate determineStartDate(LocalDate startDate) {
        return startDate;
    }

    /**
     * Determines the end date for expense calculation.
     * If an end date is provided, it is used; otherwise, the current date is used.
     *
     * @param endDate the provided end date
     * @return the determined end date
     */
    private LocalDate determineEndDate(LocalDate endDate) {
        return endDate;
    }

    /**
     * Retrieves a list of InvoiceItem objects for the specified user and date range.
     *
     * @param userId the ID of the user
     * @param start  the start date of the range
     * @param end    the end date of the range
     * @return a list of InvoiceItem objects
     */
    private List<InvoiceItem> retrieveInvoiceItems(String userId, LocalDate start, LocalDate end) {
        if (start == null && end == null) {
            return invoiceItemRepository.findByInvoiceUserId(userId);
        } else {
            return invoiceItemRepository.findByInvoiceUserIdAndDateBetween(userId, start, end);
        }
    }

    /**
     * Calculates the total expense for each month based on a list of invoice items.
     *
     * @param invoiceItems the list of invoice items
     * @return a map of YearMonth to total expense for that month
     */
    private Map<YearMonth, Double> calculateMonthlyTotals(List<InvoiceItem> invoiceItems) {
        return invoiceItems.stream()
                .collect(Collectors.groupingBy(
                        item -> YearMonth.from(item.getDate()),
                        Collectors.summingDouble(InvoiceItem::getPrice)
                ));
    }

    /**
     * Converts a map of monthly expense totals into a list of MonthlyExpenseDto objects.
     *
     * @param monthlyTotals a map of YearMonth to total expense
     * @return a sorted list of MonthlyExpenseDto objects
     */
    private List<MonthlyExpenseDto> convertToMonthlyExpenseDtos(Map<YearMonth, Double> monthlyTotals) {
        return monthlyTotals.entrySet().stream()
                .map(entry -> new MonthlyExpenseDto(entry.getKey(), BigDecimal.valueOf(entry.getValue()).setScale(2, RoundingMode.HALF_UP)))
                .sorted(Comparator.comparing(MonthlyExpenseDto::getMonth))
                .collect(Collectors.toList());
    }
}