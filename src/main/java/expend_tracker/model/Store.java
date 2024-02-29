package expend_tracker.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "stores")
@Getter
public class Store implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    @Setter
    private Location location;

    @ManyToOne
    @JoinColumn(name = "chain_id", nullable = false)
    @Setter
    private Chain chain;

    public Store() {
    }

    public Store(Location location, Chain chain) {
        this.location = location;
        this.chain = chain;
    }
}