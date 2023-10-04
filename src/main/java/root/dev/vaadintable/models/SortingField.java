package root.dev.vaadintable.models;

import lombok.Data;

@Data
public class SortingField {
    String[] pathByFieldsNameInCamelCase;
    String fieldNameInSnakeCase;
    String sortDirection;
}
