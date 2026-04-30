package com.n11bootcamp.ecommerce.payment.interfaces.rest.mapper;

import com.n11bootcamp.ecommerce.payment.domain.model.Payment;
import com.n11bootcamp.ecommerce.payment.interfaces.rest.dto.PaymentResponse;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.id(),
                payment.orderReference(),
                payment.userId(),
                payment.amount(),
                payment.currency(),
                payment.status().name(),
                payment.iyzicoPaymentId(),
                payment.createdAt(),
                payment.updatedAt()
        );
    }
}
