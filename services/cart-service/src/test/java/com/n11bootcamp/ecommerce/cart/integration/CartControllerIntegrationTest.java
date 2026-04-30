package com.n11bootcamp.ecommerce.cart.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n11bootcamp.ecommerce.cart.application.port.out.ProductServicePort;
import com.n11bootcamp.ecommerce.cart.application.dto.VariantInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CartControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @MockBean
    private ProductServicePort productServicePort;

    @AfterEach
    void cleanup() {
        Set<String> keys = redisTemplate.keys("cart:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    @Test
    void getCart_givenEmptyCart_returnsEmptyItems() throws Exception {
        var userId = UUID.randomUUID();

        mockMvc.perform(get("/api/cart/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(userId.toString()))
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.items").isEmpty())
                .andExpect(jsonPath("$.data.totalAmount").value(0));
    }

    @Test
    void addItem_givenValidRequest_addsItemToCart() throws Exception {
        var userId = UUID.randomUUID();
        var variantId = UUID.randomUUID();
        var productId = UUID.randomUUID();

        var variantInfo = new VariantInfo(productId, variantId, "Test Ürün", "M",
                new BigDecimal("99.90"), true);
        when(productServicePort.getVariantInfo(any())).thenReturn(variantInfo);

        var request = Map.of(
                "productId", productId.toString(),
                "variantId", variantId.toString(),
                "quantity", 2
        );

        mockMvc.perform(post("/api/cart/" + userId + "/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.items[0].variantId").value(variantId.toString()))
                .andExpect(jsonPath("$.data.items[0].quantity").value(2))
                .andExpect(jsonPath("$.data.totalAmount").value(199.80));
    }

    @Test
    void clearCart_givenExistingCart_clearsCart() throws Exception {
        var userId = UUID.randomUUID();
        var variantId = UUID.randomUUID();
        var productId = UUID.randomUUID();

        var variantInfo = new VariantInfo(productId, variantId, "Test Ürün", "L",
                new BigDecimal("50.00"), true);
        when(productServicePort.getVariantInfo(any())).thenReturn(variantInfo);

        var request = Map.of(
                "productId", productId.toString(),
                "variantId", variantId.toString(),
                "quantity", 1
        );

        mockMvc.perform(post("/api/cart/" + userId + "/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/cart/" + userId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/cart/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items").isEmpty());
    }
}