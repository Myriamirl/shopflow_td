package com.shopflow.inventory.dto;

import com.shopflow.inventory.model.StockItem;
import lombok.Builder;

@Builder
public record StockResponse(Long productId, int quantity) {

    public static StockResponse from(StockItem item) {
        return StockResponse.builder()
                .productId(item.getProductId())
                .quantity(item.getQuantity())
                .build();
    }
}
