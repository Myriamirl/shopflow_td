package com.shopflow.order.model;

import com.shopflow.order.exception.IllegalOrderStateException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Agrégat Commande : la racine Order et ses lignes OrderLine.
 * Toute modification passe par Order, qui garantit les règles métier :
 * total toujours à jour, transitions de statut autorisées uniquement.
 *
 * Table "orders" : ORDER est un mot réservé SQL.
 */
@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "lines")   // évite de charger les lignes juste pour un log
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Identifiant du client. */
    @Column(nullable = false, length = 64)
    private String customerId;

    @Enumerated(EnumType.STRING)   // stocke "PENDING" et non 0 : lisible et robuste
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id", nullable = false)
    private List<OrderLine> lines = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    private Order(String customerId) {
        this.customerId = customerId;
        this.status = OrderStatus.PENDING;
    }

    /** Fabrique : une nouvelle commande est toujours créée en PENDING. */
    public static Order pending(String customerId) {
        return new Order(customerId);
    }

    // ---------- Comportement métier ----------

    /**
     * Ajoute un produit à la commande. Si le produit est déjà présent,
     * on augmente la quantité au lieu de créer une seconde ligne.
     */
    public void addLine(Long productId, String productName, BigDecimal unitPrice, int quantity) {
        if (status != OrderStatus.PENDING) {
            throw new IllegalOrderStateException(id, status, "ajouter une ligne");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantité doit être positive");
        }
        lines.stream()
                .filter(l -> l.getProductId().equals(productId))
                .findFirst()
                .ifPresentOrElse(
                        l -> l.increaseQuantity(quantity),
                        () -> lines.add(new OrderLine(productId, productName, unitPrice, quantity)));
        recalculateTotal();
    }

    public void confirm() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalOrderStateException(id, status, "confirmer");
        }
        status = OrderStatus.CONFIRMED;
    }

    /** Annulation possible uniquement tant que la commande est en attente. */
    public void cancel() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalOrderStateException(id, status, "annuler");
        }
        status = OrderStatus.CANCELLED;
    }

    private void recalculateTotal() {
        total = lines.stream()
                .map(OrderLine::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /** Lignes en lecture seule : impossible de les modifier sans passer par addLine. */
    public List<OrderLine> getLines() {
        return Collections.unmodifiableList(lines);
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order other)) return false;
        return id != null && Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
