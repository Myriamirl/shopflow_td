package com.shopflow.order.service;

import com.shopflow.order.client.CatalogGateway;
import com.shopflow.order.client.ProductView;
import com.shopflow.order.dto.OrderLineRequest;
import com.shopflow.order.dto.OrderResponse;
import com.shopflow.order.dto.PlaceOrderRequest;
import com.shopflow.order.exception.CatalogUnavailableException;
import com.shopflow.order.exception.InvalidOrderException;
import com.shopflow.order.model.Order;
import com.shopflow.order.model.OrderStatus;
import com.shopflow.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    OrderRepository repository;

    @Mock
    CatalogGateway catalog;

    @InjectMocks
    OrderService service;

    private static PlaceOrderRequest request(OrderLineRequest... lines) {
        return PlaceOrderRequest.builder().customerId("c-1").lines(List.of(lines)).build();
    }

    private static OrderLineRequest line(long productId, int quantity) {
        return OrderLineRequest.builder().productId(productId).quantity(quantity).build();
    }

    @Test
    void place_copies_name_and_price_from_catalog() {
        when(catalog.getActiveProduct(1L))
                .thenReturn(new ProductView(1L, "KB-MECA-01", "Clavier mécanique", new BigDecimal("89.90"), true));
        when(repository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponse response = service.place(request(line(1L, 2)));

        assertThat(response.status()).isEqualTo(OrderStatus.PENDING);
        assertThat(response.total()).isEqualByComparingTo("179.80");
        assertThat(response.lines()).singleElement().satisfies(l -> {
            assertThat(l.productName()).isEqualTo("Clavier mécanique");
            assertThat(l.unitPrice()).isEqualByComparingTo("89.90");
        });
    }

    @Test
    void catalog_is_called_once_per_distinct_product() {
        when(catalog.getActiveProduct(1L))
                .thenReturn(new ProductView(1L, "KB-MECA-01", "Clavier", new BigDecimal("10.00"), true));
        when(repository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        service.place(request(line(1L, 1), line(1L, 3)));

        verify(catalog, times(1)).getActiveProduct(1L);
    }

    @Test
    void invalid_product_prevents_saving() {
        when(catalog.getActiveProduct(99L)).thenThrow(new InvalidOrderException("Produit inconnu : 99"));

        assertThatThrownBy(() -> service.place(request(line(99L, 1))))
                .isInstanceOf(InvalidOrderException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void catalog_down_prevents_saving() {
        when(catalog.getActiveProduct(1L)).thenThrow(new CatalogUnavailableException(new RuntimeException("timeout")));

        assertThatThrownBy(() -> service.place(request(line(1L, 1))))
                .isInstanceOf(CatalogUnavailableException.class);
        verify(repository, never()).save(any());
    }
}
