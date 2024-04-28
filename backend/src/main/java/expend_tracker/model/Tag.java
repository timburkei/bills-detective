package expend_tracker.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "tags")
@Getter
public class Tag implements Serializable {

     @Serial
     private static final long serialVersionUID = 1L;

     @Id
     @Setter
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

     @Setter
     @Column(nullable = false, unique = true, length = 255)
     private String name;

     public Tag() {

     }

     public Tag(String name) {
          this.name = name;
     }

}