package root.dev.vaadintable.models;

import com.vaadin.flow.data.provider.QuerySortOrder;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseFilter {
    int limit;
    int offset;
    int page;
    List<QuerySortOrder> sortOrders;
}
