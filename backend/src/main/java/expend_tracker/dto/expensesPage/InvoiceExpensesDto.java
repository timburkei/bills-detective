package expend_tracker.dto.expensesPage;

import lombok.AllArgsConstructor;
import lombok.Data;


import java.time.LocalDate;

/**
 * DTO for {@link expend_tracker.model.InvoiceFile} used in the invoiceFilePage.
 */
@Data

public class InvoiceExpensesDto {
    /**
     * The id of the invoice file.
     */
    private Long id;

    /**
     * The upload date of the invoice file.
     */
    private LocalDate date;

   /**
     * The chainName of the invoice file.
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
    private String zip;

    /**
     *  The city of the chain.
     */
    private String city;

    /**
     * The invoice file id.
     */
    private Long invoiceFileId;

    public InvoiceExpensesDto() {
    }

   public InvoiceExpensesDto(Long id, LocalDate date, String chainName, String street, String number, String zip, String city, Long invoiceFileId) {
        this.id = id;
        this.date = date;
        this.chainName = chainName;
        this.street = street;
        this.number = number;
        this.zip = zip;
        this.city = city;
        this.invoiceFileId = invoiceFileId;
    }
}
