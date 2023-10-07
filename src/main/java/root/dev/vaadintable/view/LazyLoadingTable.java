package root.dev.vaadintable.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Command;
import root.dev.vaadintable.entities.Product;
import root.dev.vaadintable.entities.ProductFile;
import root.dev.vaadintable.repositories.ProductFileRepository;
import root.dev.vaadintable.repositories.ProductRepository;
import root.dev.vaadintable.services.FileUploadReceiver;
import root.dev.vaadintable.services.ProductDataProvider;
import root.dev.vaadintable.services.ProductFilter;
import root.dev.vaadintable.services.ProductService;

import java.util.concurrent.atomic.AtomicReference;

@Route("lazy")
public class LazyLoadingTable extends Div {

    /**
     * Из-за того, что мы лочим UI для обновления таблицы пришлось обернуть текстовые поля в Атомики
     * (специальные объекты Java из библиотеки java.util.concurrent, чтобы обеспечить разделяемый доступ к этим ресурсам,
     * если убрать AtomicReference, то после обновления продукта в одном окне в другом, при вызове модалки, будет ругаться,
     * что нельзя вызвать сессию без блокировки
     */
    private final AtomicReference<TextField> productNameField = new AtomicReference<>(new TextField("Name"));
    private final AtomicReference<TextField> productNumberField = new AtomicReference<>(new TextField("Number"));
    private final AtomicReference<TextField> searchField = new AtomicReference<>(buildTextField());
    private final ProductFileRepository productFileRepository;

    private final ProductRepository productRepository;

    private final ProductFilter productFilter = new ProductFilter();

    private final ConfigurableFilterDataProvider<Product, Void, ProductFilter> filterDataProvider;
    private final Grid<Product> grid = new Grid<>();
    private Dialog dialog;
    private Product selectedProduct;

    public LazyLoadingTable(ProductService productService, ProductFileRepository productFileRepository, ProductRepository productRepository) {
        this.productFileRepository = productFileRepository;
        this.productRepository = productRepository;
        ProductDataProvider dataProvider = new ProductDataProvider(productService);
        filterDataProvider = dataProvider.withConfigurableFilter();
        buildGrid();
        addGridItemEventListener();
        VerticalLayout layout = new VerticalLayout(searchField.get(), grid);
        layout.setPadding(true);
        add(layout);
    }

    private void addGridItemEventListener() {
        grid.addItemClickListener(event -> {
            selectedProduct = event.getItem();
            showDialog();
            dialog.open();
        });
    }

    private void showDialog() {
        dialog = new Dialog();
        dialog.setHeaderTitle("ProductEditor");
        VerticalLayout dialogLayout = createDialogLayout();
        dialog.add(dialogLayout);

        Button saveButton = createSaveButton(dialog);
        Button cancelButton = new Button("Cancel", e -> dialog.close());
        Button removeButton = createRemoveButton();
        dialog.getFooter().add(removeButton);
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(saveButton);

        add(dialog);
    }

    private VerticalLayout createDialogLayout() {
        productNameField.get().setValue(selectedProduct.getName());
        productNumberField.get().setValue(selectedProduct.getNumber().toString());
        VerticalLayout dialogLayout = new VerticalLayout(productNameField.get(),
                productNumberField.get());
        dialogLayout.add(createImageGallery());
        dialogLayout.add(getUpload(productFileRepository));
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        return dialogLayout;
    }

    private Button createSaveButton(Dialog dialog) {
        Button saveButton = new Button("Save", e -> dialog.close());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(event -> {
            saveProductChanges();
            closeDialogAndRefreshTable();
        });
        return saveButton;
    }

    private void saveProductChanges() {
        Product product = this.productRepository.findById(this.selectedProduct.getId()).orElseThrow(RuntimeException::new);
        product.setName(productNameField.get().getValue());
        product.setNumber(Integer.valueOf(productNumberField.get().getValue()));
        productRepository.save(product);

    }

    private Button createRemoveButton() {
        Button deleteButton = new Button("Delete");
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(event -> {
            productRepository.deleteById(selectedProduct.getId());
            selectedProduct = null;
            closeDialogAndRefreshTable();
        });
        return deleteButton;
    }

    private TextField buildTextField() {
        TextField searchField = new TextField();
        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeTimeout(300);
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> {
            productFilter.setSearchText(e.getValue());
            filterDataProvider.setFilter(productFilter);
        });
        return searchField;
    }

    private void buildGrid() {
        grid.setMultiSort(true);
        grid.addColumn(Product::getName, "name").setHeader("Name");
        grid.addColumn(Product::getNumber, "number").setHeader("Number");
        grid.setItems(filterDataProvider);
        addImageColumn();
    }

    private void addImageColumn() {
        grid.addComponentColumn(item -> {
            Div div = new Div();
            if (item.getProductFileIds() != null) {
                item.getProductFileIds().forEach(uuid -> {
                    Image image = new Image();
                    image.setSrc("http://localhost:8080/api/product_files/compressed/" + uuid);
                    image.setAlt("Image");
                    div.add(image);
                });
            }
            return div;
        }).setHeader("Preview Image");
    }


    private Div createImageGallery() {
        Div div = new Div();
        if (selectedProduct != null && !selectedProduct.getProductFileIds().isEmpty()) {
            selectedProduct.getProductFileIds().forEach(uuid -> {
                Image image = new Image();
                image.setSrc("http://localhost:8080/api/product_files/compressed/" + uuid);
                image.setAlt("Image");
                div.add(image);
            });
        }
        return div;
    }

    private Upload getUpload(ProductFileRepository productFileRepository) {
        FileUploadReceiver fileUploadReceiver = new FileUploadReceiver();
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
            file.setProduct(selectedProduct);
            productFileRepository.save(file);
            closeDialogAndRefreshTable();
        });
        return upload;
    }

    private void closeDialogAndRefreshTable() {
        dialog.close();
        /**
         * Следующий код должен обновлять интерфейс во всех сессиях, т.к. UI - разделяемый ресурс, мы его лочим через ui.access
         * Но чуда не происходит....
         */
        getUI().ifPresent(ui -> ui.access((Command) () -> {
            grid.getDataProvider().refreshAll();
        }));
    }
}
