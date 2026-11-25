package com.shopflow.catalog.exception;

/** Levée quand aucun produit ne correspond à l'identifiant demandé (HTTP 404). */
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Long id) {
        super("Aucun produit avec l'identifiant " + id);
    }
}
