package expend_tracker.dto.expensesPage;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * DTO for expenses page
 */
@Data
public class ProductExpensesDto {

    /**
     * Invoice id
     */
    private List<InvoiceItemDto> invoiceItems;

    /**
     * Map of invoice item expenditure
     */
    private Map<String, Double> invoiceItemExpenditure;

}
