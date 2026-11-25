package com.shopflow.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/** Corps d'un PUT /api/v1/stock/{productId} : la nouvelle quantité en stock. */
@Builder
public record StockUpdateRequest(
        @NotNull(message = "La quantité est obligatoire")
        @Min(value = 0, message = "La quantité ne peut pas être négative")
        Integer quantity
) {
}
