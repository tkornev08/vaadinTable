package root.dev.vaadintable.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilesStorage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Basic
    @Column(columnDefinition = "LONGBLOB", name = "data")
    private byte[] data;
    private Integer size;
    private String name;
    private String mimeType;
    private UUID productId;
}
