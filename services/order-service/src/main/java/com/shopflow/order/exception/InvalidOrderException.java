package com.shopflow.order.exception;

/** La commande est bien formée mais impossible à accepter : produit inconnu ou inactif (HTTP 422). */
public class InvalidOrderException extends RuntimeException {

    public InvalidOrderException(String message) {
        super(message);
    }
}
