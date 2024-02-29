package expend_tracker.service.dashboardPage;

import expend_tracker.dto.dashboardPage.TagExpenseDto;
import expend_tracker.model.InvoiceItem;
import expend_tracker.repositories.InvoiceItemRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for calculating expenses based on tags.
 * This service provides functionalities to compute and retrieve expense data
 * categorized by product tags for a given user over a specified date range.
 */
@Service
public class TagExpenseService {

    private static final Logger log = LogManager.getLogger(TagExpenseService.class);

    /**
     * Repository for accessing invoice item data.
     */
    private final InvoiceItemRepository invoiceItemRepository;

    /**
     * Constructs a new TagExpenseService with the given InvoiceItemRepository.
     *
     * @param invoiceItemRepository the repository used for accessing invoice item data
     */
    @Autowired
    public TagExpenseService(InvoiceItemRepository invoiceItemRepository) {
        this.invoiceItemRepository = invoiceItemRepository;
    }

    /**
     * Calculates the tag-based expenses for a given user between the specified start and end dates.
     * It aggregates the expenses by tags and returns a list of TagExpenseDto objects representing the totals.
     * In case of an error, an empty list is returned.
     *
     * @param userId    the ID of the user for whom the expenses are calculated
     * @param startDate the starting date of the period for calculating expenses
     * @param endDate   the ending date of the period for calculating expenses
     * @return a list of TagExpenseDto objects representing the expenses categorized by tags,
     *         or an empty list in case of an error
     */
    public List<TagExpenseDto> calculateTagExpenses(String userId, LocalDate startDate, LocalDate endDate) {
        try {
            log.info("Calculating tag expenses for user: {}", userId);
            List<InvoiceItem> invoiceItems = fetchInvoiceItems(userId, startDate, endDate);
            List<TagExpenseDto> tagExpenses = aggregateExpensesByTag(invoiceItems);
            log.info("Tag expenses calculated successfully for user: {}", userId);
            return tagExpenses;
        } catch (Exception e) {
            log.error("Error calculating tag expenses for user: {}", userId, e);
            return Collections.emptyList();
        }
    }

    /**
     * Retrieves a list of InvoiceItem objects for the specified user and date range.
     *
     * @param userId    the ID of the user
     * @param startDate the start date of the period (inclusive)
     * @param endDate   the end date of the period (inclusive)
     * @return a list of InvoiceItem objects
     */
    private List<InvoiceItem> fetchInvoiceItems(String userId, LocalDate startDate, LocalDate endDate) {
        return (startDate != null && endDate != null)
                ? invoiceItemRepository.findByInvoiceUserIdAndInvoiceInvoiceDateBetween(userId, startDate, endDate)
                : invoiceItemRepository.findByInvoiceUserId(userId);
    }

    /**
     * Aggregates expenses by tags based on a list of invoice items.
     *
     * @param invoiceItems the list of invoice items
     * @return a list of TagExpenseDto objects representing expenses aggregated by tags
     */
    private List<TagExpenseDto> aggregateExpensesByTag(List<InvoiceItem> invoiceItems) {
        return invoiceItems.stream()
                .collect(Collectors.groupingBy(
                        invoiceItem -> invoiceItem.getProduct().getTag().getName(),
                        Collectors.summingDouble(InvoiceItem::getPrice)))
                .entrySet()
                .stream()
                .map(entry -> new TagExpenseDto(entry.getKey(),
                        BigDecimal.valueOf(entry.getValue()).setScale(2, RoundingMode.HALF_UP).doubleValue()))
                .collect(Collectors.toList());
    }
}