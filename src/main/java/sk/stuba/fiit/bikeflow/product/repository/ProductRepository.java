package sk.stuba.fiit.bikeflow.product.repository;

import sk.stuba.fiit.bikeflow.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
}
