package com.shopflow.catalog.service;

import com.shopflow.catalog.dto.ProductRequest;
import com.shopflow.catalog.dto.ProductResponse;
import com.shopflow.catalog.exception.DuplicateSkuException;
import com.shopflow.catalog.exception.ProductNotFoundException;
import com.shopflow.catalog.exception.SkuMismatchException;
import com.shopflow.catalog.model.Product;
import com.shopflow.catalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Logique métier du catalogue.
 * Le service reçoit et renvoie des DTO : l'entité Product ne sort jamais de cette couche.
 *
 * Lombok : @RequiredArgsConstructor génère le constructeur pour les champs final
 * (injection par constructeur), @Slf4j crée le logger "log".
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository repository;

    /** Liste paginée des produits actifs. */
    public Page<ProductResponse> findAll(Pageable pageable) {
        return repository.findByActiveTrue(pageable).map(ProductResponse::from);
    }

    /** Détail d'un produit (actif ou non : order-service a besoin de savoir s'il est inactif). */
    public ProductResponse findById(Long id) {
        return ProductResponse.from(getOrThrow(id));
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        if (repository.existsBySku(request.sku())) {
            throw new DuplicateSkuException(request.sku());
        }
        Product product = Product.builder()
                .sku(request.sku())
                .name(request.name().trim())
                .description(request.description())
                .price(request.price())
                .build();
        Product saved = repository.save(product);
        log.info("Produit créé : id={}, sku={}", saved.getId(), saved.getSku());
        return ProductResponse.from(saved);
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = getOrThrow(id);
        if (!product.getSku().equals(request.sku())) {
            throw new SkuMismatchException(product.getSku(), request.sku());
        }
        product.update(request.name().trim(), request.description(), request.price());
        // Pas besoin d'appeler save() : l'entité est gérée, les changements sont
        // enregistrés à la fin de la transaction (dirty checking).
        log.info("Produit mis à jour : id={}", id);
        return ProductResponse.from(product);
    }

    /** Suppression logique : le produit passe à active = false. */
    @Transactional
    public void deactivate(Long id) {
        Product product = getOrThrow(id);
        product.deactivate();
        log.info("Produit désactivé : id={}", id);
    }

    private Product getOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }
}
