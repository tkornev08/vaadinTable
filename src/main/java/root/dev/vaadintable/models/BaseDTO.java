package root.dev.vaadintable.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseDTO {
    private String id;
    private String fullName;
    private String shortName;
}
