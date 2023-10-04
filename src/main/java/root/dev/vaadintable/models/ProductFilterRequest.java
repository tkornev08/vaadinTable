package root.dev.vaadintable.models;

import lombok.*;
import lombok.experimental.FieldDefaults;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductFilterRequest extends BaseFilter {
    String filter;
    String name;
    Integer number;
}
