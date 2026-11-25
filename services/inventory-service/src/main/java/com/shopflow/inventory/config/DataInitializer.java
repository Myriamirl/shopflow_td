package com.shopflow.inventory.config;

import com.shopflow.inventory.model.StockItem;
import com.shopflow.inventory.repository.StockItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Stock initial des 10 produits du catalogue (mêmes identifiants que catalog-service).
 * Quelques stocks sont volontairement faibles : produit 3 (écran) : 5,
 * produit 8 (ordinateur portable) : 2, produit 10 (chaise) : 3.
 */
@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final StockItemRepository repository;

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }
        List<StockItem> items = List.of(
                new StockItem(1L, 10), new StockItem(2L, 10), new StockItem(3L, 5),
                new StockItem(4L, 10), new StockItem(5L, 10), new StockItem(6L, 10),
                new StockItem(7L, 10), new StockItem(8L, 2), new StockItem(9L, 10),
                new StockItem(10L, 3));
        repository.saveAll(items);
        log.info("Stock initial chargé pour {} produits", items.size());
    }
}
