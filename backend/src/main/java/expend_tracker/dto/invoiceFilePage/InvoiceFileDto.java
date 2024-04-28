package expend_tracker.dto.invoiceFilePage;

import lombok.Data;

import java.sql.Time;
import java.time.LocalDate;

/**
 * DTO for {@link expend_tracker.model.InvoiceFile}.
 */
@Data
public class InvoiceFileDto {

    private Long id;
    private boolean uploadSuccessful;
    private LocalDate uploadDate;
    private Time uploadTime;
    private String invoiceUrl;

    public InvoiceFileDto() {
    }

    public InvoiceFileDto(Long id, boolean uploadSuccessful, LocalDate uploadDate, Time uploadTime, String invoiceUrl) {
        this.id = id;
        this.uploadSuccessful = uploadSuccessful;
        this.uploadDate = uploadDate;
        this.uploadTime = uploadTime;
        this.invoiceUrl = invoiceUrl;
    }


}