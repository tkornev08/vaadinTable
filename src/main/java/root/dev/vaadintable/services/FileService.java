package root.dev.vaadintable.services;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;
import root.dev.vaadintable.entities.ProductFile;
import root.dev.vaadintable.repositories.ProductFileRepository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final ProductFileRepository productFileRepository;


    public void getOriginalFileById(UUID id, HttpServletResponse response) {
        ProductFile productFile = productFileRepository.findById(id).orElseThrow(RuntimeException::new);
        byte[] imageData = productFile.getData();
        try {
            response.setContentType(productFile.getMimeType());
            response.getOutputStream().write(imageData);
        } catch (Exception e) {
            log.error("Произошла ошибка при получении изображения без сжатия", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public void getCompressedFileById(UUID id, HttpServletResponse response) {
        ProductFile productFile = productFileRepository.findById(id).orElseThrow(RuntimeException::new);
        byte[] imageData = productFile.getData();
        try {
            byte[] scaledImageBytes = scaleImage(imageData, 100, 100);
            response.setContentType(productFile.getMimeType());
            response.getOutputStream().write(scaledImageBytes);
        } catch (Exception e) {
            log.error("Произошла ошибка при получении изображения со сжатием", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private byte[] scaleImage(byte[] imageData, int width, int height) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageData));
        BufferedImage scaledImage = Scalr.resize(bufferedImage, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, width, height);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(scaledImage, "jpg", byteArrayOutputStream); // Здесь указан формат (jpg), измените его по необходимости
        return byteArrayOutputStream.toByteArray();
    }


    public void create() {

    }

    public boolean delete(String id) {
        UUID uuid = UUID.fromString(id);
        if (productFileRepository.findById(uuid).isPresent()) {
            productFileRepository.deleteById(uuid);
            return true;
        }
        return false;
    }
}
