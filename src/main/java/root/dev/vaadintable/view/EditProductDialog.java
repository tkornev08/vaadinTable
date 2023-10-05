package root.dev.vaadintable.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import org.springframework.transaction.annotation.Transactional;
import root.dev.vaadintable.entities.Product;
import root.dev.vaadintable.entities.ProductFile;
import root.dev.vaadintable.repositories.ProductFileRepository;
import root.dev.vaadintable.services.FileUploadReceiver;
import root.dev.vaadintable.services.ProductService;

import java.util.List;
import java.util.UUID;


public class EditProductDialog extends Dialog {

    private final Grid<Product> grid;

    private final TextField nameField = new TextField("Name");
    private final IntegerField numberField = new IntegerField("Number");
    private final Button saveButton = new Button("Save");
    private final Button cancelButton = new Button("Cancel");
    private final Button deleteButton = new Button("Delete");


    public EditProductDialog(
            ProductFileRepository productFileRepository,
            UUID productId,
            ProductService productRepository,
            Grid<Product> grid) {
        this.grid = grid;
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);
        configureSaveButton(productId, productRepository);
        configureDeleteButton(productId, productRepository);
        configureCancelButton();
        Div nameFieldDiv = new Div();
        nameFieldDiv.add(nameField);
        Div numberFieldDiv = new Div();
        numberFieldDiv.add(numberField);
        add(nameFieldDiv, numberFieldDiv, saveButton, cancelButton);
        FileUploadReceiver fileUploadReceiver = new FileUploadReceiver();
        Upload upload = getUpload(productFileRepository, fileUploadReceiver, productId);
        Div div = getImageGallery(productFileRepository, productId);
        add(nameFieldDiv, numberFieldDiv);
        add(upload);
        add(div);
        add(saveButton, cancelButton, deleteButton);
    }

    private void configureCancelButton() {
        cancelButton.addClickListener(event -> close());
    }

    private void configureSaveButton(UUID productId, ProductService productRepository) {
        saveButton.addClickListener(event -> {
            saveProductChanges(productId, productRepository);
            this.grid.getDataProvider().refreshAll();
            close();
        });
    }

    private void configureDeleteButton(UUID productId, ProductService productRepository) {
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(event -> {
            deleteProduct(productId, productRepository);
            this.grid.getDataProvider().refreshAll();
            close();
        });
    }

    private void deleteProduct(UUID productId, ProductService productRepository) {
        productRepository.delete(productId);
    }

    @Transactional
    public void saveProductChanges(UUID productId, ProductService productRepository) {
        productRepository.save(productId, nameField.getValue(), numberField.getValue());
    }

    private static Div getImageGallery(ProductFileRepository productFileRepository, UUID productId) {
        Div div = new Div();
        List<UUID> uuidsByProductId = productFileRepository.findUuidsByProductId(productId);
        uuidsByProductId.forEach(uuid -> {
            Image image = new Image();
            image.setSrc("http://localhost:8080/api/product_files/compressed/"+uuid);
            image.setAlt("Image");
            div.add(image);
        });
        return div;
    }

    private static Upload getUpload(ProductFileRepository productFileRepository, FileUploadReceiver fileUploadReceiver, UUID productId) {
        Upload upload = new Upload(fileUploadReceiver);
        upload.setAcceptedFileTypes("image/tiff", ".tiff", ".jpeg", ".jpg", ".png", ".gif");
        upload.addFileRejectedListener(event -> {
            String errorMessage = event.getErrorMessage();

            Notification notification = Notification.show(errorMessage, 5000,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        });

        upload.addSucceededListener(event -> {
            byte[] fileData = fileUploadReceiver.getFileData();
            ProductFile file = new ProductFile();
            file.setData(fileData);
            file.setSize(fileData.length);
            file.setName(event.getFileName());
            file.setMimeType(event.getMIMEType());
            setProduct(productId, file);

            productFileRepository.save(file);
        });
        return upload;
    }

    private static void setProduct(UUID productId, ProductFile file) {
        Product product = new Product();
        product.setId(productId);
        file.setProduct(product);
    }

    public void setItem(Product product) {
        nameField.setValue(product.getName());
        numberField.setValue(product.getNumber());
    }
}


