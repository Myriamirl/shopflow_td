package com.shopflow.inventory.service;

import com.shopflow.inventory.dto.StockResponse;
import com.shopflow.inventory.exception.StockItemNotFoundException;
import com.shopflow.inventory.model.StockItem;
import com.shopflow.inventory.repository.StockItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    StockItemRepository repository;

    @InjectMocks
    StockService service;

    @Test
    void returns_the_stock_of_a_product() {
        when(repository.findById(1L)).thenReturn(Optional.of(new StockItem(1L, 10)));

        StockResponse response = service.findByProductId(1L);

        assertThat(response.quantity()).isEqualTo(10);
    }

    @Test
    void unknown_product_raises_not_found() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByProductId(999L)).isInstanceOf(StockItemNotFoundException.class);
    }

    @Test
    void update_creates_the_stock_line_when_missing() {
        when(repository.findById(11L)).thenReturn(Optional.empty());
        when(repository.save(any(StockItem.class))).thenAnswer(inv -> inv.getArgument(0));

        StockResponse response = service.updateQuantity(11L, 7);

        assertThat(response.productId()).isEqualTo(11L);
        assertThat(response.quantity()).isEqualTo(7);
    }
}
