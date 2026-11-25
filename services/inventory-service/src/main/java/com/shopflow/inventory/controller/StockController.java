package com.shopflow.inventory.controller;

import com.shopflow.inventory.dto.StockResponse;
import com.shopflow.inventory.dto.StockUpdateRequest;
import com.shopflow.inventory.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stock")
@RequiredArgsConstructor
@Tag(name = "Stock", description = "Stocks des produits ShopFlow")
public class StockController {

    private final StockService service;

    @GetMapping
    @Operation(summary = "Lister le stock de tous les produits")
    public List<StockResponse> list() {
        return service.findAll();
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Stock d'un produit")
    public StockResponse get(@PathVariable Long productId) {
        return service.findByProductId(productId);
    }

    @PutMapping("/{productId}")
    @Operation(summary = "Fixer la quantité en stock d'un produit")
    public StockResponse update(@PathVariable Long productId, @Valid @RequestBody StockUpdateRequest request) {
        return service.updateQuantity(productId, request.quantity());
    }
}
