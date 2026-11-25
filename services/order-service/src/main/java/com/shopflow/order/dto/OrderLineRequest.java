package com.shopflow.order.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

/** Une ligne demandée par le client : quel produit, en quelle quantité. Pas de prix : c'est le catalogue qui fait foi. */
@Builder
public record OrderLineRequest(

        @NotNull(message = "L'identifiant du produit est obligatoire")
        @Positive(message = "L'identifiant du produit doit être positif")
        Long productId,

        @NotNull(message = "La quantité est obligatoire")
        @Min(value = 1, message = "La quantité minimale est 1")
        @Max(value = 100, message = "La quantité maximale est 100")
        Integer quantity
) {
}
