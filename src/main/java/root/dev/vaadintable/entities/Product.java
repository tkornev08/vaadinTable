package root.dev.vaadintable.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Indexed;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "products")
@Indexed
public class Product {
    @Id
    @GeneratedValue()
    private UUID id;
    private String name;
    private Integer number;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_files", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "id")
    private List<UUID> productFileIds = new ArrayList<>();
}
