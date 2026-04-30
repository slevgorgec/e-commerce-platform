package com.n11bootcamp.ecommerce.payment.application.port.out;

import java.math.BigDecimal;
import java.util.UUID;

public interface IyzicoGatewayPort {

    IyzicoCheckoutResult initiateCheckout(UUID orderReference, UUID userId, BigDecimal amount, String currency);

    record IyzicoCheckoutResult(boolean success, String paymentId, String responseJson) {}
}