package com.n11bootcamp.ecommerce.order.infrastructure.persistence.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.n11bootcamp.ecommerce.order.application.port.out.OrderRepositoryPort;
import com.n11bootcamp.ecommerce.order.domain.exception.DomainException;
import com.n11bootcamp.ecommerce.order.domain.model.Order;
import com.n11bootcamp.ecommerce.order.domain.model.OrderItem;
import com.n11bootcamp.ecommerce.order.domain.model.OrderStatus;
import com.n11bootcamp.ecommerce.order.domain.model.ShippingAddress;
import com.n11bootcamp.ecommerce.order.infrastructure.persistence.entity.OrderEntity;
import com.n11bootcamp.ecommerce.order.infrastructure.persistence.entity.OrderItemEntity;
import com.n11bootcamp.ecommerce.order.infrastructure.persistence.entity.OrderStatusHistoryEntity;
import com.n11bootcamp.ecommerce.order.infrastructure.persistence.repository.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepositoryPort {

    private final OrderJpaRepository jpaRepository;
    private final ObjectMapper objectMapper;

    @Override
    public Order save(Order order) {
        // Mevcut entity'yi bul veya yeni oluştur
        var entity = jpaRepository.findById(order.id())
                .orElseGet(() -> {
                    var newEntity = new OrderEntity();
                    newEntity.setId(order.id());
                    newEntity.setUserId(order.userId());
                    newEntity.setCreatedAt(order.createdAt());
                    return newEntity;
                });

        // Önceki status geçişi için history kaydet
        var previousStatus = entity.getStatus();
        entity.setStatus(order.status().name());
        entity.setTotalAmount(order.totalAmount());
        entity.setShippingAddress(serializeShippingAddress(order.shippingAddress()));
        entity.setUpdatedAt(order.updatedAt());

        // Status geçişi varsa history ekle
        if (previousStatus != null && !previousStatus.equals(order.status().name())) {
            var history = new OrderStatusHistoryEntity();
            history.setId(UUID.randomUUID());
            history.setOrder(entity);
            history.setFromStatus(previousStatus);
            history.setToStatus(order.status().name());
            history.setChangedAt(Instant.now());
            entity.getStatusHistory().add(history);
        } else if (previousStatus == null) {
            // İlk kayıt - PENDING durumu için initial history
            var history = new OrderStatusHistoryEntity();
            history.setId(UUID.randomUUID());
            history.setOrder(entity);
            history.setFromStatus(null);
            history.setToStatus(order.status().name());
            history.setChangedAt(Instant.now());
            entity.getStatusHistory().add(history);
        }

        // Items yalnızca yeni sipariş kaydında eklenir (orphanRemoval = true)
        if (entity.getItems().isEmpty() && order.items() != null) {
            for (var item : order.items()) {
                var itemEntity = new OrderItemEntity();
                itemEntity.setId(item.id() != null ? item.id() : UUID.randomUUID());
                itemEntity.setOrder(entity);
                itemEntity.setProductId(item.productId());
                itemEntity.setVariantId(item.variantId());
                itemEntity.setProductNameSnapshot(item.productNameSnapshot());
                itemEntity.setVariantValueSnapshot(item.variantValueSnapshot());
                itemEntity.setUnitPriceSnapshot(item.unitPriceSnapshot());
                itemEntity.setQuantity(item.quantity());
                entity.getItems().add(itemEntity);
            }
        }

        var saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Order> findByIdAndUserId(UUID id, UUID userId) {
        return jpaRepository.findByIdAndUserId(id, userId).map(this::toDomain);
    }

    @Override
    public Page<Order> findByUserId(UUID userId, Pageable pageable) {
        return jpaRepository.findByUserId(userId, pageable).map(this::toDomain);
    }

    private Order toDomain(OrderEntity entity) {
        var items = entity.getItems().stream()
                .map(this::itemToDomain)
                .toList();

        var shippingAddress = deserializeShippingAddress(entity.getShippingAddress());

        return new Order(
                entity.getId(),
                entity.getUserId(),
                OrderStatus.valueOf(entity.getStatus()),
                entity.getTotalAmount(),
                shippingAddress,
                items,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private OrderItem itemToDomain(OrderItemEntity entity) {
        return new OrderItem(
                entity.getId(),
                entity.getOrder().getId(),
                entity.getProductId(),
                entity.getVariantId(),
                entity.getProductNameSnapshot(),
                entity.getVariantValueSnapshot(),
                entity.getUnitPriceSnapshot(),
                entity.getQuantity()
        );
    }

    private String serializeShippingAddress(ShippingAddress address) {
        try {
            return objectMapper.writeValueAsString(address);
        } catch (JsonProcessingException e) {
            throw new DomainException("ShippingAddress serializasyon hatası", e);
        }
    }

    private ShippingAddress deserializeShippingAddress(String json) {
        try {
            return objectMapper.readValue(json, ShippingAddress.class);
        } catch (JsonProcessingException e) {
            throw new DomainException("ShippingAddress deserializasyon hatası", e);
        }
    }
}