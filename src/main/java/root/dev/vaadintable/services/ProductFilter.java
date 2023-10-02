package root.dev.vaadintable.services;

import root.dev.vaadintable.entities.Product;

public class ProductFilter {
    private String searchTerm;

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public boolean test(Product person) {
        boolean matchesFullName = matches(person.getName(), searchTerm);
        boolean matchesProfession = matches(person.getNumber().toString(), searchTerm);
        return matchesFullName || matchesProfession;
    }

    private boolean matches(String value, String searchTerm) {
        return searchTerm == null || searchTerm.isEmpty()
                || value.toLowerCase().contains(searchTerm.toLowerCase());
    }
}
