package com.shopflow.order.exception;

/** Aucune commande avec cet identifiant (HTTP 404). */
public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long id) {
        super("Aucune commande avec l'identifiant " + id);
    }
}
