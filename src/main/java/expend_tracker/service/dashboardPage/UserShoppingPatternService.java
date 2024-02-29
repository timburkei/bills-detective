package expend_tracker.service.dashboardPage;

import expend_tracker.dto.dashboardPage.ChainExpenseDto;
import expend_tracker.dto.dashboardPage.UserShoppingPatternDto;
import expend_tracker.model.Invoice;
import expend_tracker.repositories.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class UserShoppingPatternService {

    private static final Logger log = LogManager.getLogger(UserShoppingPatternService.class);

    private final InvoiceRepository invoiceRepository;

    @Autowired
    public UserShoppingPatternService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    /**
     * Calculates the shopping pattern for a user.
     *
     * @param userId    The ID of the user.
     * @param startDate The start date for filtering expenses.
     * @param endDate   The end date for filtering expenses.
     * @return The shopping pattern for the user.
     */
    public UserShoppingPatternDto calculateUserShoppingPattern(String userId, LocalDate startDate, LocalDate endDate) {
        try {
            log.info("Calculating shopping pattern for user: " + userId);
            List<Invoice> invoices = getInvoices(userId, startDate, endDate);

            String mostFrequentTimeGroup = calculateMostFrequentTimeGroup(invoices);
            String mostFrequentWeekday = calculateMostFrequentWeekday(invoices);
            ChainExpenseDto mostFrequentChain = calculateMostFrequentChain(userId, startDate, endDate);

            return createUserShoppingPatternDto(mostFrequentTimeGroup, mostFrequentWeekday, mostFrequentChain);
        } catch (Exception e) {
            log.error("Error calculating shopping pattern for user: " + userId, e);
            throw e;
        }
    }

    private List<Invoice> getInvoices(String userId, LocalDate startDate, LocalDate endDate) {
        return startDate != null && endDate != null
                ? invoiceRepository.findByUserIdAndInvoiceDateBetween(userId, startDate, endDate)
                : invoiceRepository.findByUserId(userId);
    }

    private String calculateMostFrequentTimeGroup(List<Invoice> invoices) {
        Map<TimeGroup, Long> timeGroupCounts = invoices.stream()
                .collect(Collectors.groupingBy(invoice -> TimeGroup.fromHour(invoice.getInvoiceTime().toLocalTime().getHour()), Collectors.counting()));
        return timeGroupCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .map(TimeGroup::getLabel)
                .orElse(null);
    }

    private String calculateMostFrequentWeekday(List<Invoice> invoices) {
        Map<String, Long> weekdayCounts = invoices.stream()
                .collect(Collectors.groupingBy(invoice -> invoice.getWeekday().name(), Collectors.counting()));
        return weekdayCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private ChainExpenseDto calculateMostFrequentChain(String userId, LocalDate startDate, LocalDate endDate) {
        List<ChainExpenseDto> chainExpenses = invoiceRepository.calculateExpensesForUserAndPeriod(userId, startDate, endDate);
        return chainExpenses.stream()
                .max(Comparator.comparing(ChainExpenseDto::getTotalExpense))
                .orElse(null);
    }

    private UserShoppingPatternDto createUserShoppingPatternDto(String mostFrequentTimeGroup, String mostFrequentWeekday, ChainExpenseDto mostFrequentChain) {
        UserShoppingPatternDto userShoppingPattern = new UserShoppingPatternDto();
        userShoppingPattern.setTimeGroup(mostFrequentTimeGroup);
        userShoppingPattern.setWeekday(mostFrequentWeekday);
        if (mostFrequentChain != null) {
            userShoppingPattern.setChainId(mostFrequentChain.getChainId());
            userShoppingPattern.setChainName(mostFrequentChain.getChainName());
        }
        return userShoppingPattern;
    }
}