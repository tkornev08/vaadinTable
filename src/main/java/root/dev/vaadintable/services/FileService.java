package root.dev.vaadintable.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import root.dev.vaadintable.entities.FilesStorage;
import root.dev.vaadintable.repositories.FilesStorageRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FilesStorageRepository filesStorageRepository;

    public List<FilesStorage> findByProductId(UUID productId) {
        return filesStorageRepository.findAllByProductId(productId);
    }


    public void create() {

    }

    public boolean delete(String id) {
        UUID uuid = UUID.fromString(id);
        if (filesStorageRepository.findById(uuid).isPresent()) {
            filesStorageRepository.deleteById(uuid);
            return true;
        }
        return false;
    }

}
