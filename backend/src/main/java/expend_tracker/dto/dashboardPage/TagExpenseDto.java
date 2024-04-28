package expend_tracker.dto.dashboardPage;

import lombok.Data;

/**
 * DTO for {@link expend_tracker.model.InvoiceFile} used in the invoiceFilePage.
 */
@Data
public class TagExpenseDto {

    /**
     * The id of the invoice file.
     */
    private String categoryName;

    /**
     * The total Expense of the Tag.
     */
    private Double totalExpense;

    public TagExpenseDto(String categoryName, Double totalExpense) {
        this.categoryName = categoryName;
        this.totalExpense = totalExpense;
    }
}
