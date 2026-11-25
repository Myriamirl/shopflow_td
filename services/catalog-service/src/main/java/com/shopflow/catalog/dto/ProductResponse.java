package com.shopflow.catalog.dto;

import com.shopflow.catalog.model.Product;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO de sortie : ce que l'API renvoie pour un produit.
 * C'est le contrat public du service ; l'entité JPA peut évoluer sans le casser.
 * Un record est déjà immuable avec constructeur, accesseurs, equals et toString :
 * Lombok n'ajoute ici que @Builder, pratique dans les tests.
 */
@Builder
public record ProductResponse(
        Long id,
        String sku,
        String name,
        String description,
        BigDecimal price,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {

    /** Conversion entité -> DTO. */
    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .active(product.isActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
