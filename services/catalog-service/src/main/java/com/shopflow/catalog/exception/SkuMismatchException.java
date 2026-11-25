package com.shopflow.catalog.exception;

/** Levée quand un PUT tente de modifier le SKU, qui est immuable (HTTP 422). */
public class SkuMismatchException extends RuntimeException {

    public SkuMismatchException(String expected, String received) {
        super("Le SKU est immuable : attendu " + expected + ", reçu " + received);
    }
}
