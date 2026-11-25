package com.shopflow.order.client;

import java.math.BigDecimal;

/**
 * Vue d'un produit DU POINT DE VUE d'order-service.
 * Ce n'est PAS une copie de ProductResponse de catalog-service : on ne garde que
 * les champs utiles ici. Les autres champs du JSON (description, dates…) sont ignorés,
 * car Spring Boot configure Jackson avec FAIL_ON_UNKNOWN_PROPERTIES = false.
 * On ne partage jamais de bibliothèque de DTO entre services.
 */
public record ProductView(
        Long id,
        String sku,
        String name,
        BigDecimal price,
        boolean active
) {
}
