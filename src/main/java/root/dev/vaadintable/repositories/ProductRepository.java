package root.dev.vaadintable.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import root.dev.vaadintable.entities.Product;

import javax.swing.*;
import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findAllByName(String name);


    @Query("select p from Product p where p.number = ?1")
    List<Product> findAllByNumber(int i);

}
