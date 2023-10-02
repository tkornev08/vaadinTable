package root.dev.vaadintable.services;

import com.vaadin.flow.component.upload.Receiver;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class FileUploadReceiver implements Receiver {

    private byte[] fileData;

    @Override
    public OutputStream receiveUpload(String filename, String mimeType) {
        // Создайте OutputStream для сохранения загруженного файла в памяти
        fileData = new byte[0];
        return new ByteArrayOutputStream() {
            @Override
            public void write(byte[] b, int off, int len) {
                super.write(b, off, len);
                fileData = toByteArray();
            }
        };
    }

    public byte[] getFileData() {
        return fileData;
    }
}

