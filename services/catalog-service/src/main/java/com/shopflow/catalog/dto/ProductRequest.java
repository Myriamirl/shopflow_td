package com.shopflow.catalog.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;

/**
 * DTO d'entrée pour la création (POST) et la mise à jour (PUT) d'un produit.
 * Les contraintes sont vérifiées par @Valid dans le contrôleur.
 * En PUT, le SKU envoyé doit correspondre au SKU existant (il est immuable).
 */
@Builder
public record ProductRequest(

        @NotBlank(message = "Le SKU est obligatoire")
        @Pattern(regexp = "^[A-Z0-9-]{3,40}$",
                 message = "Le SKU doit contenir 3 à 40 caractères : majuscules, chiffres ou tirets")
        String sku,

        @NotBlank(message = "Le nom est obligatoire")
        @Size(max = 120, message = "Le nom ne doit pas dépasser 120 caractères")
        String name,

        @Size(max = 1000, message = "La description ne doit pas dépasser 1000 caractères")
        String description,

        @NotNull(message = "Le prix est obligatoire")
        @DecimalMin(value = "0.01", message = "Le prix doit être strictement positif")
        @Digits(integer = 8, fraction = 2, message = "Le prix doit avoir au plus 2 décimales")
        BigDecimal price
) {
}
