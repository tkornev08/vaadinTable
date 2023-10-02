package root.dev.vaadintable.models;

import com.vaadin.flow.data.provider.QuerySortOrder;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProdFilter {
    int limit;
    int offset;
    int page;
    List<QuerySortOrder> sortOrders;
    String filter;
    String name;
    String number;
}
