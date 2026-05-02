package com.n11bootcamp.ecommerce.order.application.usecase;

import com.n11bootcamp.ecommerce.order.application.dto.CreateOrderCommand;
import com.n11bootcamp.ecommerce.order.application.port.in.CreateOrderUseCase;
import com.n11bootcamp.ecommerce.order.application.port.out.EventPublisherPort;
import com.n11bootcamp.ecommerce.order.application.port.out.OrderRepositoryPort;
import com.n11bootcamp.ecommerce.order.application.port.out.ProductServicePort;
import com.n11bootcamp.ecommerce.order.domain.exception.DomainException;
import com.n11bootcamp.ecommerce.order.domain.model.Order;
import com.n11bootcamp.ecommerce.order.domain.model.OrderItem;
import com.n11bootcamp.ecommerce.order.domain.model.ShippingAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateOrderUseCaseImpl implements CreateOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final EventPublisherPort eventPublisher;
    private final ProductServicePort productService;

    @Override
    @Transactional
    public Order execute(CreateOrderCommand command) {
        log.debug("Sipariş oluşturuluyor: userId={}, itemCount={}", command.userId(), command.items().size());

        var items = mapItems(command);
        var shippingAddress = mapShippingAddress(command.shippingAddress());
        var order = Order.create(command.userId(), items, shippingAddress);

        var saved = orderRepository.save(order);
        log.info("Sipariş oluşturuldu: orderId={}, userId={}, totalAmount={}", saved.id(), saved.userId(), saved.totalAmount());

        eventPublisher.publishOrderCreated(saved);

        return saved;
    }

    private List<OrderItem> mapItems(CreateOrderCommand command) {
        return command.items().stream()
                .map(item -> {
                    var variant = productService.getVariantInfo(item.variantId());
                    if (!variant.active()) {
                        throw new DomainException("Ürün aktif değil: variantId=" + item.variantId());
                    }
                    return new OrderItem(
                            UUID.randomUUID(),
                            null, // orderId, save sonrası entity'de set edilir
                            variant.productId(),
                            variant.variantId(),
                            variant.productName(),
                            variant.variantValue(),
                            variant.price(),  // Fiyat Product Service'ten alınır, client'tan değil
                            item.quantity()
                    );
                })
                .toList();
    }

    private ShippingAddress mapShippingAddress(CreateOrderCommand.ShippingAddressData data) {
        return new ShippingAddress(
                data.fullName(),
                data.phone(),
                data.addressLine(),
                data.city(),
                data.district(),
                data.postalCode(),
                data.country()
        );
    }
}