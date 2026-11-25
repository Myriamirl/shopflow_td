package com.shopflow.payment.exception;

/** Aucun paiement pour cette commande (HTTP 404). */
public class PaymentNotFoundException extends RuntimeException {

    public PaymentNotFoundException(Long orderId) {
        super("Aucun paiement pour la commande " + orderId);
    }
}
