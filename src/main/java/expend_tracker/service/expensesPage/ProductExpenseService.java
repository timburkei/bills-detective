package expend_tracker.service.expensesPage;

import expend_tracker.dto.expensesPage.InvoiceItemDto;
import expend_tracker.dto.expensesPage.ProductExpensesDto;
import expend_tracker.model.Chain;
import expend_tracker.model.InvoiceItem;
import expend_tracker.model.Location;
import expend_tracker.repositories.InvoiceItemRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.time.format.DateTimeFormatter;

/**
 * Service for calculating product expenses.
 */
@Service
public class ProductExpenseService {
    private static final Logger log = LogManager.getLogger(ProductExpenseService.class);
    private final InvoiceItemRepository invoiceItemRepository;

    @Autowired
    public ProductExpenseService(InvoiceItemRepository invoiceItemRepository) {
        this.invoiceItemRepository = invoiceItemRepository;
    }

    /**
     * Calculates expenses for a given product, user and date range.
     *
     * @param productId The product ID
     * @param userId The user ID
     * @param startDate Start date for the expense calculation
     * @param endDate End date for the expense calculation
     * @return ProductExpensesDto containing expense details
     */
    public ProductExpensesDto calculateExpenses(Long productId, String userId, LocalDate startDate, LocalDate endDate) {
        try {
            log.info("Calculating expenses for product ID: {}, User ID: {} between {} and {}", productId, userId, startDate, endDate);
            List<InvoiceItem> invoiceItems = fetchInvoiceItems(productId, userId, startDate, endDate);
            return processInvoiceItems(invoiceItems);
        } catch (Exception e) {
            log.error("Error calculating expenses for product ID: {}, User ID: {}", productId, userId, e);
            return new ProductExpensesDto();
        }
    }

    private List<InvoiceItem> fetchInvoiceItems(Long productId, String userId, LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null) {
            return invoiceItemRepository.findByProductIdAndUserIdAndDateBetween(productId, userId, startDate, endDate);
        } else {
            return invoiceItemRepository.findByProductIdAndUserId(productId, userId);
        }
    }

    private ProductExpensesDto processInvoiceItems(List<InvoiceItem> invoiceItems) {
        List<InvoiceItemDto> invoiceItemDtos = new ArrayList<>();
        Map<YearMonth, Double> monthlyExpenditures = new HashMap<>();

        for (InvoiceItem invoiceItem : invoiceItems) {
            InvoiceItemDto dto = mapToInvoiceItemDto(invoiceItem);
            invoiceItemDtos.add(dto);
            YearMonth yearMonth = YearMonth.from(invoiceItem.getDate());
            monthlyExpenditures.merge(yearMonth, invoiceItem.getPrice(), Double::sum);
        }

        return buildProductExpensesDto(invoiceItemDtos, monthlyExpenditures);
    }

    private InvoiceItemDto mapToInvoiceItemDto(InvoiceItem invoiceItem) {
        InvoiceItemDto dto = new InvoiceItemDto();
        dto.setId(invoiceItem.getId());
        dto.setName(invoiceItem.getProduct().getName());
        dto.setPrice(invoiceItem.getPrice());
        dto.setDate(invoiceItem.getDate());

        Chain chain = invoiceItem.getStore().getChain();
        if (chain != null) {
            dto.setChainName(chain.getName());
        }

        Location location = invoiceItem.getStore().getLocation();
        if (location != null) {
            dto.setStreet(location.getStreet());
            dto.setNumber(location.getNumber());
            dto.setZip(location.getZip());
            dto.setCity(location.getCity());
        }

        return dto;
    }

    private ProductExpensesDto buildProductExpensesDto(List<InvoiceItemDto> invoiceItemDtos, Map<YearMonth, Double> monthlyExpenditures) {
        ProductExpensesDto productExpensesDto = new ProductExpensesDto();
        productExpensesDto.setInvoiceItems(invoiceItemDtos);

        Map<String, Double> formattedMonthlyExpenditures = new HashMap<>();
        for (Map.Entry<YearMonth, Double> entry : monthlyExpenditures.entrySet()) {
            formattedMonthlyExpenditures.put(entry.getKey().format(DateTimeFormatter.ofPattern("MM.yyyy")), entry.getValue());
        }
        productExpensesDto.setInvoiceItemExpenditure(formattedMonthlyExpenditures);

        return productExpensesDto;
    }
}