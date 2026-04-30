package com.n11bootcamp.ecommerce.notification.application.port.in;

import java.math.BigDecimal;
import java.util.UUID;

public interface NotificationPort {

    void notifyUserRegistered(UUID userId, String email, String firstName);

    void notifyOrderCreated(UUID orderId, UUID userId, BigDecimal totalAmount);

    void notifyOrderConfirmed(UUID orderId, UUID userId);

    void notifyOrderCancelled(UUID orderId, UUID userId, String reason);

    void notifyPaymentCompleted(UUID orderReference, UUID userId, BigDecimal amount);

    void notifyPaymentFailed(UUID orderReference, UUID userId, String failureReason);
}