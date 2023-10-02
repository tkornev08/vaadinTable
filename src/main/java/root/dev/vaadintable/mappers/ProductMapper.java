package root.dev.vaadintable.mappers;


import org.springframework.jdbc.core.RowMapper;
import root.dev.vaadintable.entities.Product;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ProductMapper implements RowMapper<Product> {
    @Override
    public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
        Product product = new Product();
        product.setId(UUID.fromString(rs.getString("id")));
        product.setName(rs.getString("name"));
        product.setNumber(rs.getInt("number"));
        return product;
    }
}
