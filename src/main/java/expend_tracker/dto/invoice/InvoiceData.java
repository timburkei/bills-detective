package expend_tracker.dto.invoice;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * DTO for invoice
 */
@Data
public class InvoiceData {

    /**
     * Invoice File id
     */
    private Long invoiceFileId;

    /**
     * User id
     */
    private String userId;

    /**
     * Invoice Date
     */
    private String invoiceDate;

    /**
     * Invoice Time
     */
    private LocalTime invoiceTime;

    /**
     * Total Amount for the invoice
     */
    private double totalAmount;

    /**
     * Taxes 7%
     */
    private double taxes7Amount;

    /**
     * Taxes 19%
     */
    private double taxes19Amount;

    /**
     * Discounts
     */
    private double discountsAmount;

    /**
     * Street of the store
     */
    private String streetStore;

    /**
     * Number of the store
     */
    private String numberStore;

    /**
     * Zip of the store
     */
    private int zipStore;

    /**
     * City of the store
     */
    private String cityStore;

    /**
     * Name of the store
     */
    private String nameStore;

    /**
     * List of all Invoice items
     */
    private List<InvoiceItemData> invoiceItems;
}
