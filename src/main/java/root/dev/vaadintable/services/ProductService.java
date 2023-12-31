package root.dev.vaadintable.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import root.dev.vaadintable.entities.Product;
import root.dev.vaadintable.models.ProductFilterRequest;
import root.dev.vaadintable.repositories.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productsRepository;
    private final EntityManager entityManager;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final FilterSupportService<Product> productFilterSupportService;
    @Getter
    @Setter
    private int limit;
    @Getter
    @Setter
    private int offset;

    @Transactional
    public Product save(UUID id, String name, Integer number) {
        Product product = productsRepository.findById(id).orElseThrow(RuntimeException::new);
        product.setNumber(number);
        product.setName(name);
        return productsRepository.save(product);
    }


    public List<Product> find(ProductFilterRequest filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);
        cq.where(getCriteriaPredicate(filter, root, cb));
        productFilterSupportService.setSorting(filter, cb, cq, root);
        TypedQuery<Product> query = entityManager.createQuery(cq);
        productFilterSupportService.setPagination(filter, query);
        return query.getResultList();
    }

    private Expression<Boolean> getCriteriaPredicate(ProductFilterRequest filter, Root<Product> root, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        if (filter.getName() != null && !filter.getName().isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + filter.getName().trim().toLowerCase() + "%"));
            /**
             * Попытка реализовать полнотекстовый поиск через ts_vector.
             * Реализовывается через доп запрос, т.к. Criteria API не поддерживает оператор @@
             * По времени выполняется столько же, что и через %, а может и дольше
             */
//            String sql = "SELECT id FROM products WHERE to_tsvector(name) @@ plainto_tsquery(:name)";
//            SqlParameterSource namedParameters = new MapSqlParameterSource("name", filter.getName());
//            predicates.add(root.get("id").in(this.jdbcTemplate.queryForList(sql, namedParameters, UUID.class)));
        }
        if (filter.getNumber() != null) {
            predicates.add(cb.equal(root.get("number"), filter.getNumber()));
        }
        return cb.and(predicates.toArray(new Predicate[0]));
    }

    public Long getCount(ProductFilterRequest filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Product> countRoot = countQuery.from(Product.class);
        countQuery.select(cb.count(countRoot)).where(getCriteriaPredicate(filter, countRoot, cb));
        return entityManager.createQuery(countQuery).getSingleResult();
    }

}
