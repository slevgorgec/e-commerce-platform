package com.n11bootcamp.ecommerce.cart.infrastructure.persistence.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.n11bootcamp.ecommerce.cart.application.port.out.CartRepositoryPort;
import com.n11bootcamp.ecommerce.cart.domain.model.Cart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class CartRedisAdapter implements CartRepositoryPort {

    private static final String CART_KEY_PREFIX = "cart:";
    private static final String EVENT_KEY_PREFIX = "processed_event:";
    private static final Duration CART_TTL = Duration.ofDays(30);
    private static final Duration EVENT_TTL = Duration.ofHours(24);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public CartRedisAdapter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public Optional<Cart> findByUserId(UUID userId) {
        var key = cartKey(userId);
        var json = redisTemplate.opsForValue().get(key);
        if (json == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(json, Cart.class));
        } catch (Exception e) {
            log.error("Sepet deserialize edilemedi: userId={}", userId, e);
            return Optional.empty();
        }
    }

    @Override
    public Cart save(Cart cart) {
        var key = cartKey(cart.userId());
        try {
            var json = objectMapper.writeValueAsString(cart);
            redisTemplate.opsForValue().set(key, json, CART_TTL);
        } catch (Exception e) {
            log.error("Sepet kaydedilemedi: userId={}", cart.userId(), e);
            throw new RuntimeException("Sepet kaydedilemedi", e);
        }
        return cart;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        redisTemplate.delete(cartKey(userId));
    }

    @Override
    public boolean isEventProcessed(String eventId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(eventKey(eventId)));
    }

    @Override
    public void markEventProcessed(String eventId) {
        redisTemplate.opsForValue().set(eventKey(eventId), "1", EVENT_TTL);
    }

    private String cartKey(UUID userId) {
        return CART_KEY_PREFIX + userId;
    }

    private String eventKey(String eventId) {
        return EVENT_KEY_PREFIX + eventId;
    }
}
