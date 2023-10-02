package root.dev.vaadintable.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import root.dev.vaadintable.entities.FilesStorage;

import java.util.List;
import java.util.UUID;

public interface FilesStorageRepository extends JpaRepository<FilesStorage, UUID> {

    List<FilesStorage> findAllByProductId(UUID productId);
}
