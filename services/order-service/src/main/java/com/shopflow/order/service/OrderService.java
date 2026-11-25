package com.shopflow.order.service;

import com.shopflow.order.client.CatalogGateway;
import com.shopflow.order.client.ProductView;
import com.shopflow.order.dto.OrderLineRequest;
import com.shopflow.order.dto.OrderResponse;
import com.shopflow.order.dto.PlaceOrderRequest;
import com.shopflow.order.exception.OrderNotFoundException;
import com.shopflow.order.model.Order;
import com.shopflow.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Logique métier des commandes.
 *
 * Point clé : place() n'est PAS annotée @Transactional. Les appels réseau vers
 * catalog-service sont faits AVANT d'ouvrir la transaction : on ne garde pas une
 * connexion à la base ouverte pendant qu'on attend un autre service.
 * La sauvegarde (repository.save) ouvre sa propre transaction, courte.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final CatalogGateway catalog;

    public OrderResponse place(PlaceOrderRequest request) {
        // 1. Interroger le catalogue une seule fois par produit distinct
        Map<Long, ProductView> products = new LinkedHashMap<>();
        for (OrderLineRequest line : request.lines()) {
            products.computeIfAbsent(line.productId(), catalog::getActiveProduct);
        }

        // 2. Construire l'agrégat : nom et prix sont copiés depuis le catalogue
        Order order = Order.pending(request.customerId());
        for (OrderLineRequest line : request.lines()) {
            ProductView p = products.get(line.productId());
            order.addLine(p.id(), p.name(), p.price(), line.quantity());
        }

        // 3. Enregistrer la commande et ses lignes (cascade)
        Order saved = repository.save(order);
        log.info("Commande {} créée pour {} : {} ligne(s), total {} EUR",
                saved.getId(), saved.getCustomerId(), saved.getLines().size(), saved.getTotal());
        return OrderResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public OrderResponse findById(Long id) {
        return OrderResponse.from(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> findByCustomer(String customerId, Pageable pageable) {
        return repository.findByCustomerId(customerId, pageable).map(OrderResponse::from);
    }

    @Transactional
    public OrderResponse cancel(Long id) {
        Order order = getOrThrow(id);
        order.cancel();   // lève IllegalOrderStateException si la commande n'est plus PENDING
        log.info("Commande {} annulée", id);
        return OrderResponse.from(order);
    }

    private Order getOrThrow(Long id) {
        return repository.findWithLinesById(id).orElseThrow(() -> new OrderNotFoundException(id));
    }
}
