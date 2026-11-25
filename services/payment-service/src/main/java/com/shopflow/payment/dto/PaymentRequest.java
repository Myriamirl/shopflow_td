package com.shopflow.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

/** Demande de paiement d'une commande. */
@Builder
public record PaymentRequest(
        @NotNull(message = "L'identifiant de commande est obligatoire")
        Long orderId,

        @NotBlank(message = "Le client est obligatoire")
        String customerId,

        @NotNull(message = "Le montant est obligatoire")
        @DecimalMin(value = "0.01", message = "Le montant doit être strictement positif")
        BigDecimal amount
) {
}
