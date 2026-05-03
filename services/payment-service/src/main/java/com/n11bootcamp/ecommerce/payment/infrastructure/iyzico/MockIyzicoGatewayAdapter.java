package com.n11bootcamp.ecommerce.payment.infrastructure.iyzico;

import com.n11bootcamp.ecommerce.payment.application.port.out.IyzicoGatewayPort;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Devre dışı — gerçek entegrasyon IyzicoCheckoutFormAdapter'da.
 */
@Slf4j
public class MockIyzicoGatewayAdapter implements IyzicoGatewayPort {

    @Override
    public IyzicoCheckoutResult initiateCheckout(UUID orderReference, UUID userId,
                                                  BigDecimal amount, String currency) {
        throw new UnsupportedOperationException("Mock devre dışı");
    }

    @Override
    public IyzicoRetrieveResult retrievePaymentResult(String token) {
        throw new UnsupportedOperationException("Mock devre dışı");
    }
}
