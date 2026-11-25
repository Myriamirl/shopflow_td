package com.shopflow.order.dto;

import com.shopflow.order.model.OrderLine;
import lombok.Builder;

import java.math.BigDecimal;

/** Une ligne telle que renvoyée par l'API. */
@Builder
public record OrderLineResponse(
        Long productId,
        String productName,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal lineTotal
) {

    public static OrderLineResponse from(OrderLine line) {
        return OrderLineResponse.builder()
                .productId(line.getProductId())
                .productName(line.getProductName())
                .unitPrice(line.getUnitPrice())
                .quantity(line.getQuantity())
                .lineTotal(line.lineTotal())
                .build();
    }
}
