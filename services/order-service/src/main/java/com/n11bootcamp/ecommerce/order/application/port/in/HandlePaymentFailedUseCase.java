package com.n11bootcamp.ecommerce.order.application.port.in;

import java.util.UUID;

/**
 * PaymentFailed eventi alındığında siparişi CANCELLED durumuna günceller.
 */
public interface HandlePaymentFailedUseCase {

    void execute(UUID orderId);
}