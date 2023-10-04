package root.dev.vaadintable.services;

import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import root.dev.vaadintable.models.BaseFilter;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilterSupportService<T> {

    public void setPagination(BaseFilter filter, Query query) {
        query.setFirstResult((filter.getOffset()));
        query.setMaxResults(filter.getLimit());
    }

    public void setSorting(BaseFilter filter, CriteriaBuilder cb, CriteriaQuery<T> cq, Root<T> root) {
        if (filter.getSortOrders() != null && !filter.getSortOrders().isEmpty()) {
            List<Order> orders = new ArrayList<>();
            for (QuerySortOrder sortOrder : filter.getSortOrders() ) {
                if (sortOrder.getDirection() == SortDirection.DESCENDING) {
                    orders.add(cb.desc(root.get(sortOrder.getSorted())));
                } else {
                    orders.add(cb.asc(root.get(sortOrder.getSorted())));
                }
            }
            cq.orderBy(orders);
        }
    }
}
