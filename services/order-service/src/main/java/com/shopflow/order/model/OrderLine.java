package com.shopflow.order.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Ligne de commande. Elle n'existe qu'à l'intérieur d'une commande :
 * on ne la crée et on ne la modifie qu'à travers l'agrégat Order.
 *
 * Le nom et le prix du produit sont COPIÉS au moment de l'achat :
 * une hausse de prix future dans le catalogue ne doit pas modifier une commande passée.
 */
@Entity
@Table(name = "order_line")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class OrderLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Identifiant du produit dans catalog-service (pas de clé étrangère : autre base). */
    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false, length = 120)
    private String productName;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private int quantity;

    /** Visibilité package : seule la classe Order peut créer une ligne. */
    OrderLine(Long productId, String productName, BigDecimal unitPrice, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    /** Ajoute une quantité (même produit commandé deux fois dans la requête). */
    void increaseQuantity(int extra) {
        this.quantity += extra;
    }

    /** Montant de la ligne : prix unitaire x quantité, arrondi au centime. */
    public BigDecimal lineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderLine other)) return false;
        return id != null && Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
