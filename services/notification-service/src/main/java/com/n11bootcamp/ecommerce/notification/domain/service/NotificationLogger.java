package com.n11bootcamp.ecommerce.notification.domain.service;

import com.n11bootcamp.ecommerce.notification.application.port.in.NotificationPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * V1: mock notification — all channels write to log only.
 * Replace individual methods with real email/SMS calls in future versions.
 */
@Slf4j
@Service
public class NotificationLogger implements NotificationPort {

    public void notifyUserRegistered(UUID userId, String email, String firstName) {
        log.info("[NOTIFICATION] Hoş geldiniz! Kullanıcı kaydı tamamlandı. userId={}, email={}, ad={}",
                userId, email, firstName);
    }

    public void notifyOrderCreated(UUID orderId, UUID userId, BigDecimal totalAmount) {
        log.info("[NOTIFICATION] Siparişiniz alındı. orderId={}, userId={}, tutar={}",
                orderId, userId, totalAmount);
    }

    public void notifyOrderConfirmed(UUID orderId, UUID userId) {
        log.info("[NOTIFICATION] Siparişiniz onaylandı! orderId={}, userId={}", orderId, userId);
    }

    public void notifyOrderCancelled(UUID orderId, UUID userId, String reason) {
        log.info("[NOTIFICATION] Siparişiniz iptal edildi. orderId={}, userId={}, sebep={}",
                orderId, userId, reason);
    }

    public void notifyPaymentCompleted(UUID orderReference, UUID userId, BigDecimal amount) {
        log.info("[NOTIFICATION] Ödemeniz alındı. orderReference={}, userId={}, tutar={}",
                orderReference, userId, amount);
    }

    public void notifyPaymentFailed(UUID orderReference, UUID userId, String failureReason) {
        log.warn("[NOTIFICATION] Ödeme başarısız. orderReference={}, userId={}, sebep={}",
                orderReference, userId, failureReason);
    }
}
