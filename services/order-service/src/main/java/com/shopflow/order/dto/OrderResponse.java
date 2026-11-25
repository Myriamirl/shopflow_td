package com.shopflow.order.dto;

import com.shopflow.order.model.Order;
import com.shopflow.order.model.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/** DTO de sortie : la commande et ses lignes. */
@Builder
public record OrderResponse(
        Long id,
        String customerId,
        OrderStatus status,
        BigDecimal total,
        List<OrderLineResponse> lines,
        Instant createdAt,
        Instant updatedAt
) {

    public static OrderResponse from(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomerId())
                .status(order.getStatus())
                .total(order.getTotal())
                .lines(order.getLines().stream().map(OrderLineResponse::from).toList())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
