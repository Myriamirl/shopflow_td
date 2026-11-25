package com.shopflow.catalog.service;

import com.shopflow.catalog.dto.ProductRequest;
import com.shopflow.catalog.dto.ProductResponse;
import com.shopflow.catalog.exception.DuplicateSkuException;
import com.shopflow.catalog.exception.ProductNotFoundException;
import com.shopflow.catalog.model.Product;
import com.shopflow.catalog.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository repository;

    @InjectMocks
    ProductService service;

    private final ProductRequest request = ProductRequest.builder()
            .sku("KB-MECA-01")
            .name("Clavier mécanique")
            .description("Switchs rouges")
            .price(new BigDecimal("89.90"))
            .build();

    @Test
    void create_saves_product_when_sku_is_free() {
        when(repository.existsBySku("KB-MECA-01")).thenReturn(false);
        when(repository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductResponse response = service.create(request);

        assertThat(response.sku()).isEqualTo("KB-MECA-01");
        assertThat(response.price()).isEqualByComparingTo("89.90");
        assertThat(response.active()).isTrue();
    }

    @Test
    void create_rejects_duplicate_sku() {
        when(repository.existsBySku("KB-MECA-01")).thenReturn(true);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(DuplicateSkuException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void deactivate_sets_active_to_false() {
        Product product = Product.builder().sku("KB-MECA-01").name("Clavier").price(new BigDecimal("10.00")).build();
        when(repository.findById(1L)).thenReturn(Optional.of(product));

        service.deactivate(1L);

        assertThat(product.isActive()).isFalse();
    }

    @Test
    void findById_throws_when_product_is_missing() {
        when(repository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(42L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("42");
    }
}
