package expend_tracker.service.dashboardPage;
import expend_tracker.dto.dashboardPage.ChainExpenseDto;
import expend_tracker.repositories.InvoiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Service class for calculating chain-based expenses.
 * This service provides functionalities to compute and retrieve expense data
 * per chain for a given user over a specified date range.
 */
@Service
public class ChainExpenseService {

    private static final Logger log = LoggerFactory.getLogger(ChainExpenseService.class);

    /**
     * Repository for accessing invoice data.
     */
    private final InvoiceRepository invoiceRepository;

    /**
     * Constructs a new ChainExpenseService with the given InvoiceRepository.
     *
     * @param invoiceRepository the repository used for accessing invoice data
     */
    @Autowired
    public ChainExpenseService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    /**
     * Calculates the expenses for a user within a specific chain between the specified start and end dates.
     * It aggregates the expenses and returns a list of ChainExpenseDto objects representing the totals.
     * In case of an error, an empty list is returned.
     *
     * @param userId    the ID of the user for whom the expenses are calculated
     * @param startDate the starting date of the period for calculating expenses
     * @param endDate   the ending date of the period for calculating expenses
     * @return a list of ChainExpenseDto objects representing the expenses, or an empty list in case of an error
     */
    public List<ChainExpenseDto> calculateExpenses(String userId, LocalDate startDate, LocalDate endDate) {
        try {
            log.info("Starting to calculate expenses for user: {} from {} to {}", userId, startDate, endDate);
            List<ChainExpenseDto> expenses = fetchExpenses(userId, startDate, endDate);
            for (ChainExpenseDto expense : expenses) {
                double roundedExpense = roundToTwoDecimalPlaces(expense.getTotalExpense());
                expense.setTotalExpense(roundedExpense);
            }
            log.info("Successfully calculated expenses for user: {}", userId);
            return expenses;
        } catch (Exception e) {
            log.error("Error occurred while calculating expenses for user: {}", userId, e);
            return Collections.emptyList();
        }
    }

    /**
     * Fetches the expenses for a user within a specific period.
     * This method retrieves the data from the InvoiceRepository.
     *
     * @param userId    the ID of the user
     * @param startDate the start date of the period
     * @param endDate   the end date of the period
     * @return a list of ChainExpenseDto objects representing the expenses
     */
    private List<ChainExpenseDto> fetchExpenses(String userId, LocalDate startDate, LocalDate endDate) {
        return invoiceRepository.calculateExpensesForUserAndPeriod(userId, startDate, endDate);
    }


    public double roundToTwoDecimalPlaces(double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}