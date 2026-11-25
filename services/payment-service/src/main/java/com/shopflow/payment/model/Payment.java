package com.shopflow.payment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * Résultat du paiement d'une commande.
 * orderId est UNIQUE : une commande ne peut être payée qu'une seule fois.
 */
@Entity
@Table(name = "payment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long orderId;

    @Column(nullable = false, length = 64)
    private String customerId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(length = 255)
    private String reason;

    @Column(nullable = false)
    private Instant createdAt;

    private Payment(Long orderId, String customerId, BigDecimal amount, PaymentStatus status, String reason) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.status = status;
        this.reason = reason;
        this.createdAt = Instant.now();
    }

    public static Payment accepted(Long orderId, String customerId, BigDecimal amount) {
        return new Payment(orderId, customerId, amount, PaymentStatus.ACCEPTED, null);
    }

    public static Payment refused(Long orderId, String customerId, BigDecimal amount, String reason) {
        return new Payment(orderId, customerId, amount, PaymentStatus.REFUSED, reason);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Payment other)) return false;
        return id != null && Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
