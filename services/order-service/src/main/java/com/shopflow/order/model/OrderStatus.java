package com.shopflow.order.model;

/**
 * Cycle de vie d'une commande.
 * PENDING : créée, en attente de traitement.
 * CONFIRMED : stock réservé et paiement accepté (pas encore branché dans cette version).
 * CANCELLED : annulée par le client ou suite à un échec.
 */
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    CANCELLED
}
