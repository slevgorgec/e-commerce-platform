package com.n11bootcamp.ecommerce.order.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class OrderControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("order_db_test")
            .withUsername("postgres")
            .withPassword("postgres");

    @Container
    static RabbitMQContainer rabbitmq = new RabbitMQContainer("rabbitmq:3.12-management-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.rabbitmq.host", rabbitmq::getHost);
        registry.add("spring.rabbitmq.port", rabbitmq::getAmqpPort);
        registry.add("eureka.client.enabled", () -> false);
        registry.add("spring.cloud.config.enabled", () -> false);
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createOrder_givenValidRequest_returns201() throws Exception {
        var userId = UUID.randomUUID();
        String json = """
                {
                    "items": [
                        {
                            "productId": "%s",
                            "variantId": "%s",
                            "productName": "Test Ürün",
                            "variantValue": "M",
                            "unitPrice": 199.90,
                            "quantity": 2
                        }
                    ],
                    "shippingAddress": {
                        "fullName": "Ahmet Yılmaz",
                        "phone": "05321234567",
                        "addressLine": "Test Mahallesi, Test Sokak No:1",
                        "city": "İstanbul",
                        "district": "Kadıköy",
                        "postalCode": "34700",
                        "country": "Türkiye"
                    }
                }
                """.formatted(UUID.randomUUID(), UUID.randomUUID());

        mockMvc.perform(post("/api/orders")
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.userId").value(userId.toString()))
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.items[0].productNameSnapshot").value("Test Ürün"));
    }

    @Test
    void getOrder_givenNonExistingId_returns404() throws Exception {
        var userId = UUID.randomUUID();
        var nonExistingId = UUID.randomUUID();

        mockMvc.perform(get("/api/orders/{id}", nonExistingId)
                        .header("X-User-Id", userId.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    @Test
    void listOrders_givenUserWithNoOrders_returnsEmptyPage() throws Exception {
        var userId = UUID.randomUUID();

        mockMvc.perform(get("/api/orders")
                        .header("X-User-Id", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(0));
    }

    @Test
    void createOrder_givenMissingItems_returns400() throws Exception {
        var userId = UUID.randomUUID();
        String json = """
                {
                    "items": [],
                    "shippingAddress": {
                        "fullName": "Ahmet Yılmaz",
                        "phone": "05321234567",
                        "addressLine": "Test Mahallesi",
                        "city": "İstanbul",
                        "district": "Kadıköy",
                        "postalCode": "34700",
                        "country": "Türkiye"
                    }
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
}