package com.shopflow.catalog.exception;

/** Levée quand on tente de créer un produit avec un SKU déjà utilisé (HTTP 409). */
public class DuplicateSkuException extends RuntimeException {

    public DuplicateSkuException(String sku) {
        super("Un produit avec le SKU " + sku + " existe déjà");
    }
}
