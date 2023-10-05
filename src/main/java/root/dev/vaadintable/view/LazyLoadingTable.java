package root.dev.vaadintable.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import root.dev.vaadintable.entities.Product;
import root.dev.vaadintable.repositories.ProductFileRepository;
import root.dev.vaadintable.services.ProductDataProvider;
import root.dev.vaadintable.services.ProductFilter;
import root.dev.vaadintable.services.ProductService;

@Route("lazy")
public class LazyLoadingTable extends Div {

    private final ProductFileRepository productFileRepository;

    private final ProductFilter personFilter = new ProductFilter();

    private final ConfigurableFilterDataProvider<Product, Void, ProductFilter> filterDataProvider;
    private Grid<Product> grid = new Grid<>();

    public LazyLoadingTable(ProductService productService, ProductFileRepository productFileRepository) {
        this.productFileRepository = productFileRepository;
        ProductDataProvider dataProvider = new ProductDataProvider(productService);
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
            Product selectedProduct = event.getItem();
            EditProductDialog dialog = new EditProductDialog(this.productFileRepository, selectedProduct.getId(), productService, grid);
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
                    image.setSrc("http://localhost:8080/api/product_files/compressed/"+uuid);
                    image.setAlt("Image");
                    div.add(image);
                });
            }
            return div;
        }).setHeader("Preview Image");
    }
}
