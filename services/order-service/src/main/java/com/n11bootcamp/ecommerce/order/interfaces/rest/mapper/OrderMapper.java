package com.n11bootcamp.ecommerce.order.interfaces.rest.mapper;

import com.n11bootcamp.ecommerce.order.application.dto.CreateOrderCommand;
import com.n11bootcamp.ecommerce.order.domain.model.Order;
import com.n11bootcamp.ecommerce.order.interfaces.rest.dto.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * REST DTO ↔ Domain / Application DTO dönüşümleri.
 */
@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        var items = order.items().stream()
                .map(item -> new OrderItemResponse(
                        item.id(),
                        item.productId(),
                        item.variantId(),
                        item.productNameSnapshot(),
                        item.variantValueSnapshot(),
                        item.unitPriceSnapshot(),
                        item.quantity()
                ))
                .toList();

        var address = order.shippingAddress();
        var shippingAddressResponse = new ShippingAddressResponse(
                address.fullName(),
                address.phone(),
                address.addressLine(),
                address.city(),
                address.district(),
                address.postalCode(),
                address.country()
        );

        return new OrderResponse(
                order.id(),
                order.userId(),
                order.status().name(),
                order.totalAmount(),
                shippingAddressResponse,
                items,
                order.createdAt()
        );
    }

    public CreateOrderCommand toCommand(UUID userId, CreateOrderRequest request) {
        var items = request.items().stream()
                .map(item -> new CreateOrderCommand.OrderItemData(
                        item.productId(),
                        item.variantId(),
                        item.productName(),
                        item.variantValue(),
                        item.unitPrice(),
                        item.quantity()
                ))
                .toList();

        var addr = request.shippingAddress();
        var shippingAddress = new CreateOrderCommand.ShippingAddressData(
                addr.fullName(),
                addr.phone(),
                addr.addressLine(),
                addr.city(),
                addr.district(),
                addr.postalCode(),
                addr.country()
        );

        return new CreateOrderCommand(userId, items, shippingAddress);
    }
}