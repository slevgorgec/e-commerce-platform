package com.n11bootcamp.ecommerce.payment.infrastructure.iyzico;

import com.n11bootcamp.ecommerce.payment.application.port.out.IyzicoGatewayPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * V1 mock implementasyonu. Gerçek Iyzico SDK entegrasyonu bu adapter'da yapılacak.
 * Tüm ödemeler başarılı kabul edilir; gerçek entegrasyonda Iyzico Checkout Form URL'i döner.
 */
@Slf4j
@Component
public class MockIyzicoGatewayAdapter implements IyzicoGatewayPort {

    @Override
    public IyzicoCheckoutResult initiateCheckout(UUID orderReference, UUID userId,
                                                  BigDecimal amount, String currency) {
        log.info("[MOCK] Iyzico checkout başlatılıyor: orderReference={}, amount={} {}",
                orderReference, amount, currency);

        var mockPaymentId = "IYZICO-MOCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        var mockResponse = """
                {"status":"success","paymentId":"%s","price":"%s","currency":"%s","mock":true}
                """.formatted(mockPaymentId, amount, currency).trim();

        log.info("[MOCK] Iyzico ödeme onaylandı: paymentId={}", mockPaymentId);
        return new IyzicoCheckoutResult(true, mockPaymentId, mockResponse);
    }
}
