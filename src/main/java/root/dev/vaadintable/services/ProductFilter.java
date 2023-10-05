package root.dev.vaadintable.services;

import lombok.Getter;
import lombok.Setter;
import root.dev.vaadintable.entities.Product;

@Setter
@Getter
public class ProductFilter {
    private String searchText;


    public boolean test(Product person) {
        boolean matchesFullName = matches(person.getName(), searchText);
        boolean matchesProfession = matches(person.getNumber().toString(), searchText);
        return matchesFullName || matchesProfession;
    }

    private boolean matches(String value, String searchTerm) {
        return searchTerm == null || searchTerm.isEmpty()
                || value.toLowerCase().contains(searchTerm.toLowerCase());
    }
}
