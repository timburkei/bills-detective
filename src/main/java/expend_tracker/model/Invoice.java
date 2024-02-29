package expend_tracker.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Time;
import java.time.LocalDate;

@Entity
@Table(name = "invoices")
@Getter
public class Invoice implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    private LocalDate invoiceDate;

    @Setter
    @Column(nullable = false)
    private Time invoiceTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "weekday", nullable = false)
    @Setter
    private DayOfWeek weekday;

    @Setter
    private double totalAmount;

    @Setter
    private double taxes7Amount;

    @Setter
    private double taxes19Amount;

    @Setter
    private double discountAmount;

    @OneToOne
    @JoinColumn(name = "file_id", nullable = false)
    @Setter
    private InvoiceFile invoiceFile;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    @Setter
    private Store store;

    @Setter
    @Column(nullable = false)
    private String userId;

    public Invoice() {
    }

    public Invoice(
            LocalDate invoiceDate,
            Time invoiceTime,
            DayOfWeek weekday,
            double totalAmount,
            double taxes7Amount,
            double taxes19Amount,
            double discountAmount,
            InvoiceFile invoiceFile,
            Store store,
            String userId
    ) {
        if(totalAmount <= 0 || taxes7Amount < 0 || taxes19Amount < 0 || discountAmount < 0) {
            throw new IllegalArgumentException("Amount values must be non-negative.");
        }

        this.invoiceDate = invoiceDate;
        this.invoiceTime = invoiceTime;
        this.weekday = weekday;
        this.totalAmount = totalAmount;
        this.taxes7Amount = taxes7Amount;
        this.taxes19Amount = taxes19Amount;
        this.discountAmount = discountAmount;
        this.invoiceFile = invoiceFile;
        this.store = store;
        this.userId = userId;
    }

    public enum DayOfWeek {
        Montag, Dienstag, Mittwoch, Donnerstag, Freitag, Samstag, Sonntag;

        public static DayOfWeek fromJavaTimeDayOfWeek(java.time.DayOfWeek day) {
            switch(day) {
                case MONDAY: return Montag;
                case TUESDAY: return Dienstag;
                case WEDNESDAY: return Mittwoch;
                case THURSDAY: return Donnerstag;
                case FRIDAY: return Freitag;
                case SATURDAY: return Samstag;
                case SUNDAY: return Sonntag;
                default: throw new IllegalArgumentException("Unknown day: " + day);
            }
        }
    }
}