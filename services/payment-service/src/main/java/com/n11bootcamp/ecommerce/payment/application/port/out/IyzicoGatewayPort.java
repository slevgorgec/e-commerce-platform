package com.n11bootcamp.ecommerce.payment.application.port.out;

import java.math.BigDecimal;
import java.util.UUID;

public interface IyzicoGatewayPort {

    IyzicoCheckoutResult initiateCheckout(UUID orderReference, UUID userId, BigDecimal amount, String currency);

    IyzicoRetrieveResult retrievePaymentResult(String token);

    record IyzicoCheckoutResult(boolean success, String checkoutFormUrl, String iyzicoToken, String errorMessage) {}

    record IyzicoRetrieveResult(boolean success, String paymentId, String responseJson, String failureReason) {}
}