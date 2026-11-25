package com.shopflow.catalog.controller;

import com.shopflow.catalog.dto.ProductRequest;
import com.shopflow.catalog.dto.ProductResponse;
import com.shopflow.catalog.exception.ProductNotFoundException;
import com.shopflow.catalog.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@ActiveProfiles("test")
class ProductControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    ProductService service;

    @Test
    void post_valid_product_returns_201_with_location() throws Exception {
        ProductResponse created = ProductResponse.builder()
                .id(1L).sku("KB-MECA-01").name("Clavier mécanique")
                .price(new BigDecimal("89.90")).active(true)
                .createdAt(Instant.now()).updatedAt(Instant.now())
                .build();
        when(service.create(any(ProductRequest.class))).thenReturn(created);

        mvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sku":"KB-MECA-01","name":"Clavier mécanique","price":89.90}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/products/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.sku").value("KB-MECA-01"));
    }

    @Test
    void post_invalid_product_returns_400_problem_detail() throws Exception {
        mvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sku":"","name":"","price":-5}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Requête invalide"))
                .andExpect(jsonPath("$.errors.sku").exists())
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.price").exists());
    }

    @Test
    void get_unknown_product_returns_404_problem_detail() throws Exception {
        when(service.findById(99L)).thenThrow(new ProductNotFoundException(99L));

        mvc.perform(get("/api/v1/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Produit introuvable"));
    }
}
