package com.n11bootcamp.ecommerce.order.application.port.in;

import java.util.UUID;

/**
 * StockReservationFailed eventi alındığında siparişi CANCELLED durumuna günceller.
 */
public interface HandleStockReservationFailedUseCase {

    void execute(UUID orderId, String reason);
}