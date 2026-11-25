package com.shopflow.payment.exception;

/** La commande a déjà été payée (HTTP 409). */
public class PaymentAlreadyExistsException extends RuntimeException {

    public PaymentAlreadyExistsException(Long orderId) {
        super("La commande " + orderId + " a déjà un paiement");
    }
}
