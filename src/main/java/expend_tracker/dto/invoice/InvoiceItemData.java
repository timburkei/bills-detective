package expend_tracker.dto.invoice;

import lombok.Data;

/**
 * DTO for invoice item
 */
@Data
public class InvoiceItemData {

    /**
     * Invoice id
     */
    private String name;

    /**
     * Invoice Price
     */
    private double price;


    /**
     * InvoiceItem Constructor
     */
    public InvoiceItemData() {
    }

}
