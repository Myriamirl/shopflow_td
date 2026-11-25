package com.shopflow.catalog.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * Entité JPA : un produit du catalogue.
 * Elle reste interne au service : elle n'est JAMAIS exposée telle quelle par l'API
 * (on passe toujours par les DTO du package dto).
 *
 * Lombok : @Getter génère les getters, @NoArgsConstructor le constructeur vide exigé par JPA.
 * Pas de @Data ni de @Setter sur une entité : on la modifie par des méthodes métier,
 * et equals/hashCode sont écrits à la main (basés sur l'identifiant).
 */
@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String sku;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 1000)
    private String description;

    // BigDecimal pour les montants : jamais double ni float (erreurs d'arrondi)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    /** Création d'un produit : Product.builder().sku(...).name(...).price(...).build() */
    @Builder
    public Product(String sku, String name, String description, BigDecimal price) {
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.price = price;
        this.active = true;
    }

    // ---------- Comportement métier ----------

    /** Met à jour les informations modifiables du produit (le SKU est immuable). */
    public void update(String name, String description, BigDecimal price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    /** Suppression logique : le produit n'est plus vendu mais reste en base. */
    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // ---------- Égalité basée sur l'identifiant (recommandé pour les entités JPA) ----------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product other)) return false;
        return id != null && Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
