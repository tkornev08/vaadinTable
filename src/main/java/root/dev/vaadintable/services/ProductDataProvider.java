package root.dev.vaadintable.services;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import lombok.Getter;
import root.dev.vaadintable.entities.Product;
import root.dev.vaadintable.models.ProductFilterRequest;

import java.util.stream.Stream;

public class ProductDataProvider extends AbstractBackEndDataProvider<Product, ProductFilter> {

    private final ProductService productService;
    @Getter
    private Stream<Product> productStream;


    public ProductDataProvider(ProductService productService) {
        this.productService = productService;
    }

    @Override
    protected Stream<Product> fetchFromBackEnd(Query<Product, ProductFilter> query) {
        System.out.println("query.getOffset():" + query.getOffset());
        System.out.println("query.getLimit():" + query.getLimit());
        ProductFilterRequest filter = getProductFilterRequest(query);
        productStream = productService.find(filter).stream();
        return productStream;
    }

    @Override
    protected int sizeInBackEnd(Query<Product, ProductFilter> query) {
        ProductFilterRequest filter = getProductFilterRequest(query);
        return Math.toIntExact(productService.getCount(filter));
    }

    private static ProductFilterRequest getProductFilterRequest(Query<Product, ProductFilter> query) {
        ProductFilterRequest filter = new ProductFilterRequest();
        filter.setLimit(query.getLimit());
        filter.setOffset(query.getOffset());
        filter.setSortOrders(query.getSortOrders());
        return filter;
    }


}
