package expend_tracker.dto.expensesPage;

import lombok.Data;


import java.time.LocalDate;

/**
 * DTO for {@link expend_tracker.model.InvoiceFile} used in the invoiceFilePage.
 */
@Data
public class InvoiceItemDto {
    /**
     * The id of the invoice file.
     */
    private Long id;

    /**
     * The name of the invoice file.
     */
    private String name;

    /**
     * The price of the invoice file.
     */
    private Double price;

    /**
     * The date of the invoice file.
     */
    private LocalDate date;

    /**
     * The chain name of the invoice file.
     */
    private String chainName;

    /**
     * The street of the chain.
     */
    private String street;

    /**
     * The house number of the chain.
     */
    private String number;

    /**
     * The zip code of the chain.
     */
    private Integer zip;

    /**
     * The city of the chain.
     */
    private String city;
}
