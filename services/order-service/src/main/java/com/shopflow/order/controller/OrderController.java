package com.shopflow.order.controller;

import com.shopflow.order.dto.OrderResponse;
import com.shopflow.order.dto.PlaceOrderRequest;
import com.shopflow.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/** API REST des commandes, version 1. */
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Commandes", description = "Passage et suivi des commandes ShopFlow")
public class OrderController {

    private final OrderService service;

    @PostMapping
    @Operation(summary = "Passer une commande")
    public ResponseEntity<OrderResponse> place(@Valid @RequestBody PlaceOrderRequest request) {
        OrderResponse created = service.place(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);   // 201 + Location
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter une commande")
    public OrderResponse get(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping
    @Operation(summary = "Lister les commandes d'un client (paginé, plus récentes d'abord)")
    public Page<OrderResponse> listByCustomer(
            @RequestParam String customerId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.findByCustomer(customerId, pageable);
    }

    /**
     * Annuler est une ACTION métier, pas une suppression : on garde la commande
     * avec le statut CANCELLED. D'où un POST sur une sous-ressource plutôt qu'un DELETE.
     */
    @PostMapping("/{id}/cancel")
    @Operation(summary = "Annuler une commande en attente")
    public OrderResponse cancel(@PathVariable Long id) {
        return service.cancel(id);
    }
}
