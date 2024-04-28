package expend_tracker.dto.expensesPage;

import lombok.Data;

/**
 * DTO for expenses page
 */
@Data
public class InvoiceItemExpensesDto {

    /**
     * Invoice item id
     */
    private Long id;

    /**
     * Name
     */
    private String name;

    /**
     * Product Name
     */
    private String productName;

    /**
     * Price
     */
    private Double price;

    /**
     * Product Id
     */
    private Long productId;

    /**
     * Tag Id
     */
    private Long tagId;

    /**
     * Tag Name
     */
    private String tagName;

    public InvoiceItemExpensesDto() {
    }

    /**
     * Constructor
     */
    public InvoiceItemExpensesDto(Long id, String name, String productName, Double price, Long productId, Long tagId, String tagName) {
        this.id = id;
        this.name = name;
        this.productName = productName;
        this.price=price;
        this.productId = productId;
        this.tagId = tagId;
        this.tagName = tagName;
    }
}
