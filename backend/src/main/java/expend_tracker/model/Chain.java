package expend_tracker.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "chains")
@Getter
public class Chain implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    private Long id;

    @Setter
    @Column(name = "name", nullable = false, length = 255)
    private String name;


    public Chain() {
    }

    public Chain(@NotNull String name) {
        this.name = name;
    }
}