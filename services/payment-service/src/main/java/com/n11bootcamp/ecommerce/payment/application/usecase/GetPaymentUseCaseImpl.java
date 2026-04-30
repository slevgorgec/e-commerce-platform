package com.n11bootcamp.ecommerce.payment.application.usecase;

import com.n11bootcamp.ecommerce.payment.application.port.in.GetPaymentUseCase;
import com.n11bootcamp.ecommerce.payment.application.port.out.PaymentRepositoryPort;
import com.n11bootcamp.ecommerce.payment.domain.exception.PaymentNotFoundException;
import com.n11bootcamp.ecommerce.payment.domain.model.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetPaymentUseCaseImpl implements GetPaymentUseCase {

    private final PaymentRepositoryPort paymentRepository;

    @Override
    @Transactional(readOnly = true)
    public Payment getByOrderReference(UUID orderReference) {
        log.debug("Ödeme sorgulanıyor: orderReference={}", orderReference);
        return paymentRepository.findByOrderReference(orderReference)
                .orElseThrow(() -> new PaymentNotFoundException(orderReference));
    }
}
