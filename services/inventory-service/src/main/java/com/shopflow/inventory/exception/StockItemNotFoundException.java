package com.shopflow.inventory.exception;

/** Aucun stock connu pour ce produit (HTTP 404). */
public class StockItemNotFoundException extends RuntimeException {

    public StockItemNotFoundException(Long productId) {
        super("Aucun stock pour le produit " + productId);
    }
}
