package expend_tracker.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "locations")
@Getter
public class Location implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false, length = 255)
    private String street;

    @Setter
    @Column(nullable = false, length = 50)
    private String number;

    @Setter
    private int zip;

    @Setter
    @Column(nullable = false, length = 100)
    private String city;

    public Location() {
    }

    public Location(String street, String number, int zip, String city) {
        if (Integer.toString(zip).length() != 5) {
            throw new IllegalArgumentException("Zip length must contain 5 numbers.");
        }
        this.street = street;
        this.number = number;
        this.zip = zip;
        this.city = city;
    }
}