package root.dev.vaadintable.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import root.dev.vaadintable.models.ProductResponse;
import root.dev.vaadintable.repositories.FilesStorageRepository;
import root.dev.vaadintable.services.FileService;
import root.dev.vaadintable.services.ProductDataProvider;
import root.dev.vaadintable.services.ProductFilter;
import root.dev.vaadintable.services.ProductService;

import java.io.ByteArrayInputStream;

@Route("lazy")
public class LazyLoadingTable extends Div {

    private final FilesStorageRepository filesStorageRepository;

    private final ProductFilter personFilter = new ProductFilter();

    private final ConfigurableFilterDataProvider<ProductResponse, Void, ProductFilter> filterDataProvider;
    private Grid<ProductResponse> grid = new Grid<>();

    public LazyLoadingTable(ProductService productService, FilesStorageRepository filesStorageRepository, FileService fileService) {
        this.filesStorageRepository = filesStorageRepository;
        ProductDataProvider dataProvider = new ProductDataProvider(productService, fileService);
        filterDataProvider = dataProvider.withConfigurableFilter();
        buildGrid();
        TextField searchField = buildTextField();
        addGridItemEventListener(productService);
        VerticalLayout layout = new VerticalLayout(searchField, grid);
        layout.setPadding(true);
        add(layout);
    }

    private void addGridItemEventListener(ProductService productService) {
        grid.addItemClickListener(event -> {
            ProductResponse selectedProduct = event.getItem();
            EditProductDialog dialog = new EditProductDialog(this.filesStorageRepository, selectedProduct.getId(), productService, grid);
            dialog.setItem(selectedProduct);
            dialog.open();
        });
    }

    private TextField buildTextField() {
        TextField searchField = new TextField();
        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> {
            personFilter.setSearchTerm(e.getValue());
            filterDataProvider.setFilter(personFilter);
        });
        return searchField;
    }

    private void buildGrid() {
        grid.setMultiSort(true);
        grid.addColumn(ProductResponse::getName, "name").setHeader("Name");
        grid.addColumn(ProductResponse::getNumber, "number").setHeader("Number");
        grid.setItems(filterDataProvider);
        addImageColumn();
    }

    private void addImageColumn() {
        grid.addComponentColumn(item -> {
            Div div = new Div();
            if (item.getFiles()!=null && !item.getFiles().isEmpty()) {
                item.getFiles().forEach(file -> {
                    Image image = new Image();
                    image.setWidth("50px");
                    StreamResource resource = new StreamResource(file.getName(), () -> new ByteArrayInputStream(file.getData()));
                    image.setSrc(resource);
                    image.setAlt("Image");
                    div.add(image);
                });
            }
            return div;
        }).setHeader("Preview Image");
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
    }

}
