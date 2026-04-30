package com.n11bootcamp.ecommerce.notification.unit;

import com.n11bootcamp.ecommerce.notification.domain.service.NotificationLogger;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class NotificationLoggerTest {

    private final NotificationLogger notificationLogger = new NotificationLogger();

    @Test
    void notifyUserRegistered_givenValidData_logsWithoutException() {
        assertDoesNotThrow(() ->
                notificationLogger.notifyUserRegistered(UUID.randomUUID(), "test@example.com", "Ahmet"));
    }

    @Test
    void notifyOrderCreated_givenValidData_logsWithoutException() {
        assertDoesNotThrow(() ->
                notificationLogger.notifyOrderCreated(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("299.90")));
    }

    @Test
    void notifyOrderConfirmed_givenValidData_logsWithoutException() {
        assertDoesNotThrow(() ->
                notificationLogger.notifyOrderConfirmed(UUID.randomUUID(), UUID.randomUUID()));
    }

    @Test
    void notifyOrderCancelled_givenValidData_logsWithoutException() {
        assertDoesNotThrow(() ->
                notificationLogger.notifyOrderCancelled(UUID.randomUUID(), UUID.randomUUID(), "Stok yetersiz"));
    }

    @Test
    void notifyPaymentCompleted_givenValidData_logsWithoutException() {
        assertDoesNotThrow(() ->
                notificationLogger.notifyPaymentCompleted(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("499.00")));
    }

    @Test
    void notifyPaymentFailed_givenValidData_logsWithoutException() {
        assertDoesNotThrow(() ->
                notificationLogger.notifyPaymentFailed(UUID.randomUUID(), UUID.randomUUID(), "Kart reddi"));
    }
}
