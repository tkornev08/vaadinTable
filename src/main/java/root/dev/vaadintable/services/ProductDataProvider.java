package root.dev.vaadintable.services;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import lombok.Getter;
import root.dev.vaadintable.entities.FilesStorage;
import root.dev.vaadintable.models.ProductFilterRequest;
import root.dev.vaadintable.models.ProductResponse;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class ProductDataProvider extends AbstractBackEndDataProvider<ProductResponse, ProductFilter> {

    private final ProductService productService;
    private final FileService fileService;
    @Getter
    private Stream<ProductResponse> productResponseStream;


    public ProductDataProvider(ProductService productService, FileService fileService) {
        this.productService = productService;
        this.fileService = fileService;
    }

    @Override
    protected Stream<ProductResponse> fetchFromBackEnd(Query<ProductResponse, ProductFilter> query) {
        System.out.println("query.getOffset():" + query.getOffset());
        System.out.println("query.getLimit():" + query.getLimit());
        ProductFilterRequest filter = getProductFilterRequest(query);
        productResponseStream = productService.find(filter).stream().map(product ->
                ProductResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .number(product.getNumber())
                        .files(getFilesByProductId(product.getId()))
                        .build()
        );
        return productResponseStream;
    }
    @Override
    protected int sizeInBackEnd(Query<ProductResponse, ProductFilter> query) {
        ProductFilterRequest filter = getProductFilterRequest(query);
        return Math.toIntExact(productService.getCount(filter));
    }
    private static ProductFilterRequest getProductFilterRequest(Query<ProductResponse, ProductFilter> query) {
        ProductFilterRequest filter = new ProductFilterRequest();
        filter.setLimit(query.getLimit());
        filter.setOffset(query.getOffset());
        filter.setSortOrders(query.getSortOrders());
        return filter;
    }

    private List<FilesStorage> getFilesByProductId(UUID id) {
        return fileService.findByProductId(id);
    }


}
