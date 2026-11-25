package com.shopflow.inventory.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Objects;

/**
 * Stock d'un produit.
 *
 * L'identifiant est celui du produit dans catalog-service. Il n'y a pas de clé étrangère :
 * le catalogue a sa propre base, à laquelle inventory-service n'a pas accès.
 * @Version active le verrouillage optimiste : deux mises à jour simultanées du même stock
 * ne peuvent pas s'écraser l'une l'autre.
 */
@Entity
@Table(name = "stock_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class StockItem {

    @Id
    private Long productId;

    @Column(nullable = false)
    private int quantity;

    @Version
    private Long version;

    public StockItem(Long productId, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("La quantité ne peut pas être négative");
        }
        this.productId = productId;
        this.quantity = quantity;
    }

    /** Remplace la quantité en stock (inventaire, réapprovisionnement). */
    public void changeQuantity(int newQuantity) {
        if (newQuantity < 0) {
            throw new IllegalArgumentException("La quantité ne peut pas être négative");
        }
        this.quantity = newQuantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StockItem other)) return false;
        return productId != null && Objects.equals(productId, other.productId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
