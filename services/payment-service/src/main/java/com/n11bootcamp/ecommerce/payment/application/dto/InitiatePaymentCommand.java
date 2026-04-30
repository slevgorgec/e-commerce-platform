package com.n11bootcamp.ecommerce.payment.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record InitiatePaymentCommand(
        UUID orderReference,
        UUID userId,
        BigDecimal amount,
        String currency
) {}
