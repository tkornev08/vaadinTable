package root.dev.vaadintable.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import root.dev.vaadintable.entities.Product;

import javax.swing.*;
import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query("select p from Product p order by p.name limit 10000")
    List<Product> find100();

    @Query("select p from Product p where p.number = ?1")
    List<Product> findAllBy(Integer number);

    @Query("select p from Product p order by p.name ")
    List<Product> findByLimitAndOffset(int offset, int limit);
}
