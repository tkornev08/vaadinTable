package root.dev.vaadintable.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import root.dev.vaadintable.entities.ProductFile;

import java.util.List;
import java.util.UUID;

public interface ProductFileRepository extends JpaRepository<ProductFile, UUID> {

    List<ProductFile> findAllByProductId(UUID productId);

    @Query("select p.id from ProductFile p where p.productId = ?1")
    List<UUID> findUuidsByProductId(UUID productId);
}
