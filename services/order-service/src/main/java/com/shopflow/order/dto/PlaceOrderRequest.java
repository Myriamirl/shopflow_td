package com.shopflow.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

/**
 * Corps de POST /api/v1/orders.
 * @Valid sur la liste : chaque OrderLineRequest est validée elle aussi.
 */
@Builder
public record PlaceOrderRequest(

        @NotBlank(message = "L'identifiant client est obligatoire")
        @Size(max = 64, message = "L'identifiant client ne doit pas dépasser 64 caractères")
        String customerId,

        @NotEmpty(message = "La commande doit contenir au moins une ligne")
        @Size(max = 20, message = "Une commande contient au plus 20 lignes")
        List<@Valid OrderLineRequest> lines
) {
}
