package com.shopflow.order.controller;

import com.shopflow.order.dto.OrderLineResponse;
import com.shopflow.order.dto.OrderResponse;
import com.shopflow.order.dto.PlaceOrderRequest;
import com.shopflow.order.exception.CatalogUnavailableException;
import com.shopflow.order.exception.InvalidOrderException;
import com.shopflow.order.model.OrderStatus;
import com.shopflow.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@ActiveProfiles("test")
class OrderControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    OrderService service;

    private static final String VALID_BODY = """
            {"customerId":"c-1","lines":[{"productId":1,"quantity":2}]}
            """;

    @Test
    void valid_order_returns_201_with_location() throws Exception {
        OrderResponse created = OrderResponse.builder()
                .id(42L).customerId("c-1").status(OrderStatus.PENDING)
                .total(new BigDecimal("179.80"))
                .lines(List.of(OrderLineResponse.builder()
                        .productId(1L).productName("Clavier mécanique")
                        .unitPrice(new BigDecimal("89.90")).quantity(2)
                        .lineTotal(new BigDecimal("179.80")).build()))
                .createdAt(Instant.now()).updatedAt(Instant.now())
                .build();
        when(service.place(any(PlaceOrderRequest.class))).thenReturn(created);

        mvc.perform(post("/api/v1/orders").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/orders/42"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.lines[0].productName").value("Clavier mécanique"));
    }

    @Test
    void invalid_body_returns_400_with_nested_field_errors() throws Exception {
        mvc.perform(post("/api/v1/orders").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"customerId":"","lines":[{"productId":1,"quantity":0}]}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.customerId").exists())
                .andExpect(jsonPath("$.errors['lines[0].quantity']").exists());
    }

    @Test
    void inactive_product_returns_422() throws Exception {
        when(service.place(any())).thenThrow(new InvalidOrderException("Produit inactif : 1"));

        mvc.perform(post("/api/v1/orders").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.detail").value("Produit inactif : 1"));
    }

    @Test
    void catalog_down_returns_503() throws Exception {
        when(service.place(any())).thenThrow(new CatalogUnavailableException(new RuntimeException()));

        mvc.perform(post("/api/v1/orders").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.title").value("Catalogue indisponible"));
    }
}
