package com.n11bootcamp.ecommerce.order.application.port.in;

import java.util.UUID;

/**
 * PaymentCompleted eventi alındığında siparişi CONFIRMED durumuna günceller.
 */
public interface HandlePaymentCompletedUseCase {

    void execute(UUID orderId);
}