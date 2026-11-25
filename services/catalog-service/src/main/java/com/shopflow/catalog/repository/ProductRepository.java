package com.shopflow.catalog.repository;

import com.shopflow.catalog.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Accès aux données des produits. Spring Data JPA génère l'implémentation
 * à partir du nom des méthodes.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /** Liste paginée des produits actifs uniquement. */
    Page<Product> findByActiveTrue(Pageable pageable);

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);
}
