package com.shopflow.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Client HTTP déclaratif vers catalog-service.
 * OpenFeign génère l'implémentation : on écrit seulement l'interface.
 *
 * L'adresse de catalog-service est lue dans application.yml (catalog.url).
 */
@FeignClient(name = "catalog-service", url = "${catalog.url}")
public interface CatalogClient {

    @GetMapping("/api/v1/products/{id}")
    ProductView getProduct(@PathVariable("id") Long id);
}
