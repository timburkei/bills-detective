package expend_tracker.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.io.Serial;
import java.io.Serializable;
import java.sql.Time;
import java.time.LocalDate;

@Entity
@Table(name = "invoice_files")
@Getter
public class InvoiceFile implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter
    private Long id;

    @Setter
    private boolean uploadSuccessful;

    @Setter
    @Column(nullable = false)
    private LocalDate uploadDate;

    @Setter
    @Column(nullable = false)
    private Time uploadTime;

    @Setter
    @Column(nullable = false, length = 255)
    private String invoiceUrl;

    public InvoiceFile() {
    }

    public InvoiceFile(String invoiceUrl) {
        this.invoiceUrl = invoiceUrl;
    }
}