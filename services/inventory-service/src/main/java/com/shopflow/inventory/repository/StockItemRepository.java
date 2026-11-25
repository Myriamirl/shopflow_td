package com.shopflow.inventory.repository;

import com.shopflow.inventory.model.StockItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockItemRepository extends JpaRepository<StockItem, Long> {
}
