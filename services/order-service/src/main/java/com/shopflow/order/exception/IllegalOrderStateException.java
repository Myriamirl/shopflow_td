package com.shopflow.order.exception;

import com.shopflow.order.model.OrderStatus;

/** Transition de statut interdite, par exemple annuler une commande déjà confirmée (HTTP 409). */
public class IllegalOrderStateException extends RuntimeException {

    public IllegalOrderStateException(Long orderId, OrderStatus status, String action) {
        super("Impossible de " + action + " la commande " + orderId + " : statut " + status);
    }
}
