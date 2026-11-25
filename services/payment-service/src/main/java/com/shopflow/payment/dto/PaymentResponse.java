package com.shopflow.payment.dto;

import com.shopflow.payment.model.Payment;
import com.shopflow.payment.model.PaymentStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record PaymentResponse(
        Long id,
        Long orderId,
        String customerId,
        BigDecimal amount,
        PaymentStatus status,
        String reason,
        Instant createdAt
) {

    public static PaymentResponse from(Payment p) {
        return PaymentResponse.builder()
                .id(p.getId())
                .orderId(p.getOrderId())
                .customerId(p.getCustomerId())
                .amount(p.getAmount())
                .status(p.getStatus())
                .reason(p.getReason())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
