package com.shopflow.payment.controller;

import com.shopflow.payment.dto.PaymentRequest;
import com.shopflow.payment.dto.PaymentResponse;
import com.shopflow.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Paiements", description = "Paiements des commandes ShopFlow")
public class PaymentController {

    private final PaymentService service;

    /**
     * Paye une commande. Le paiement est créé (201) qu'il soit accepté ou refusé :
     * un refus est un résultat métier, pas une erreur technique.
     */
    @PostMapping
    @Operation(summary = "Payer une commande")
    public ResponseEntity<PaymentResponse> pay(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse payment = service.pay(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{orderId}").buildAndExpand(payment.orderId()).toUri();
        return ResponseEntity.created(location).body(payment);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Paiement d'une commande")
    public PaymentResponse get(@PathVariable Long orderId) {
        return service.findByOrderId(orderId);
    }
}
