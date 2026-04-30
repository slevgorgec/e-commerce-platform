package com.n11bootcamp.ecommerce.product.application.port.out;

import java.util.UUID;

public interface EventPublisherPort {
    void publishStockReserved(UUID orderId);
    void publishStockReservationFailed(UUID orderId, String reason);
    void publishStockCommitted(UUID orderId);
    void publishStockReleased(UUID orderId);
}
