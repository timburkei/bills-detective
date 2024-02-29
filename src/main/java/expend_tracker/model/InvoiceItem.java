package expend_tracker.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "invoice_items")
@Getter
public class InvoiceItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false, length = 255)
    private String name;

    @Setter
    private double price;

    @Setter
    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    @Setter
    private Store store;

    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    @Setter
    private Invoice invoice;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @Setter
    private Product product;

    public InvoiceItem() {
    }

    public InvoiceItem(String name, double price, LocalDate date, Store store, Invoice invoice, Product product) {
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be positive.");
        }

        this.name = name;
        this.price = price;
        this.date = date;
        this.store = store;
        this.invoice = invoice;
        this.product = product;
    }
}