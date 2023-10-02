package root.dev.vaadintable.services;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventBus;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import root.dev.vaadintable.entities.Product;
import root.dev.vaadintable.mappers.ProductMapper;
import root.dev.vaadintable.models.ProdFilter;
import root.dev.vaadintable.repositories.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productsRepository;
    private final JdbcTemplate jdbcTemplate;

    @Getter
    private List<Product> products;
    private ComponentEventBus eventBus = new ComponentEventBus(new Div());

    public static class ProductEvent extends ComponentEvent<Div> {

        public ProductEvent(Div source, boolean fromClient) {
            super(new Div(), false);
        }
    }

    public Registration attachListener(ComponentEventListener<ProductEvent> productListener) {
        return eventBus.addListener(ProductEvent.class, productListener);
    }

    @Transactional
    public boolean delete(UUID productId) {
        if (productsRepository.findById(productId).isPresent()) {
            productsRepository.deleteById(productId);
            return true;
        }
        return false;
    }

    @Transactional
    public Product save(UUID id, String name, Integer number) {
        Product product = productsRepository.findById(id).orElseThrow(RuntimeException::new);
        product.setNumber(number);
        product.setName(name);
        this.products.forEach(prod -> {
            if (prod.getId().equals(id)) {
                prod.setName(name);
                prod.setNumber(number);
            }
        });
        return productsRepository.save(product);
    }


    public List<Product> find(ProdFilter filter) {
        StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder countingBuilder = new StringBuilder();
        sqlBuilder.append("select id, name, number from products \n");
        countingBuilder.append("select count(*) from products \n");
        if (filter != null) {
            buildConditionPartSqlRequest(filter, sqlBuilder, countingBuilder);

            if (!filter.getSortOrders().isEmpty()) {
                StringBuilder orderBuilder = buildSortingPartOfSqlRequest(filter);
                countingBuilder.append(orderBuilder);
                sqlBuilder.append(orderBuilder);
            }
            sqlBuilder.append(" limit ").append(filter.getLimit());
            sqlBuilder.append(" offset ").append((filter.getOffset()));
        }
        System.out.println("SQL: ");
        System.out.println(sqlBuilder);
        this.products = jdbcTemplate.query(sqlBuilder.toString(), new ProductMapper());
        return this.products;
    }

    private static StringBuilder buildSortingPartOfSqlRequest(ProdFilter filter) {
        StringBuilder orderBuilder = new StringBuilder();
        orderBuilder.append("order by ");
        List<QuerySortOrder> sortOrders = filter.getSortOrders();
        for (int i = 0; i < sortOrders.size(); i++) {
            QuerySortOrder querySortOrder = sortOrders.get(i);
            orderBuilder.append(querySortOrder.getSorted());
            orderBuilder.append(" ");
            if (querySortOrder.getDirection() == SortDirection.DESCENDING) {
                orderBuilder.append("DESC");
            } else {
                orderBuilder.append("ASC");
            }
            if (i < sortOrders.size() - 1) {
                orderBuilder.append(",");
            }
        }
        return orderBuilder;
    }

    private void buildConditionPartSqlRequest(ProdFilter filter, StringBuilder sqlBuilder, StringBuilder countingBuilder) {
        List<String> predicates = new ArrayList<>();
        if (filter.getName() != null && !filter.getName().isEmpty()) {
            predicates.add("lower(name) like " + "'%" + filter.getName().trim().toLowerCase() + "%' \n");
        }
        if (filter.getNumber() != null && !filter.getNumber().isEmpty()) {
            predicates.add("lower(number) like " + "'%" + filter.getNumber().trim().toLowerCase() + "%' \n");
        }
        if (!predicates.isEmpty()) {
            String where;
            StringBuilder whereBuilder = new StringBuilder();
            for (int i = 0; i < predicates.size(); i++) {
                String predicate = predicates.get(i);
                if (i > 0) {
                    whereBuilder.append(" and ");
                }
                whereBuilder.append(predicate);
            }
            where = whereBuilder.toString();
            countingBuilder.append(where);
            sqlBuilder.append(where);
        }
    }

    public Integer count(ProdFilter filter) {
        StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder countingBuilder = new StringBuilder();
        sqlBuilder.append("select id, name, number from products \n");
        countingBuilder.append("select count(*) from products \n");
        if (filter != null) {
            buildConditionPartSqlRequest(filter, sqlBuilder, countingBuilder);
        }
        return jdbcTemplate.queryForObject(countingBuilder.toString(), Integer.class);
    }
}
