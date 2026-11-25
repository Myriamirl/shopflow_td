package com.shopflow.inventory.service;

import com.shopflow.inventory.dto.StockResponse;
import com.shopflow.inventory.exception.StockItemNotFoundException;
import com.shopflow.inventory.model.StockItem;
import com.shopflow.inventory.repository.StockItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Consultation et mise à jour du stock. */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {

    private final StockItemRepository repository;

    @Transactional(readOnly = true)
    public List<StockResponse> findAll() {
        return repository.findAll(Sort.by("productId")).stream()
                .map(StockResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public StockResponse findByProductId(Long productId) {
        return StockResponse.from(load(productId));
    }

    /**
     * Fixe la quantité en stock d'un produit.
     * Si le produit n'a pas encore de stock, la ligne est créée.
     */
    @Transactional
    public StockResponse updateQuantity(Long productId, int quantity) {
        StockItem item = repository.findById(productId)
                .orElseGet(() -> repository.save(new StockItem(productId, 0)));
        item.changeQuantity(quantity);
        log.info("Stock du produit {} : {}", productId, quantity);
        return StockResponse.from(item);
    }

    private StockItem load(Long productId) {
        return repository.findById(productId)
                .orElseThrow(() -> new StockItemNotFoundException(productId));
    }
}
