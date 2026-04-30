package com.n11bootcamp.ecommerce.payment.application.usecase;

import com.n11bootcamp.ecommerce.payment.application.dto.InitiatePaymentCommand;
import com.n11bootcamp.ecommerce.payment.application.port.in.InitiatePaymentUseCase;
import com.n11bootcamp.ecommerce.payment.application.port.out.EventPublisherPort;
import com.n11bootcamp.ecommerce.payment.application.port.out.IyzicoGatewayPort;
import com.n11bootcamp.ecommerce.payment.application.port.out.PaymentRepositoryPort;
import com.n11bootcamp.ecommerce.payment.domain.exception.DuplicatePaymentException;
import com.n11bootcamp.ecommerce.payment.domain.model.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InitiatePaymentUseCaseImpl implements InitiatePaymentUseCase {

    private final PaymentRepositoryPort paymentRepository;
    private final IyzicoGatewayPort iyzicoGateway;
    private final EventPublisherPort eventPublisher;

    @Override
    @Transactional
    public Payment execute(InitiatePaymentCommand command) {
        log.info("Ödeme başlatılıyor: orderReference={}, userId={}, amount={}",
                command.orderReference(), command.userId(), command.amount());

        if (paymentRepository.existsByOrderReference(command.orderReference())) {
            log.warn("Duplike ödeme girişimi: orderReference={}", command.orderReference());
            throw new DuplicatePaymentException(command.orderReference());
        }

        var payment = Payment.create(command.orderReference(), command.userId(), command.amount());
        var saved = paymentRepository.save(payment);

        var result = iyzicoGateway.initiateCheckout(
                command.orderReference(), command.userId(), command.amount(), command.currency());

        Payment finalPayment;
        if (result.success()) {
            finalPayment = paymentRepository.save(saved.withCompleted(result.paymentId(), result.responseJson()));
            log.info("Ödeme tamamlandı: orderReference={}, iyzicoPaymentId={}",
                    command.orderReference(), result.paymentId());
            eventPublisher.publishPaymentCompleted(
                    finalPayment.orderReference(),
                    finalPayment.userId(),
                    finalPayment.amount(),
                    result.paymentId()
            );
        } else {
            finalPayment = paymentRepository.save(saved.withFailed(result.responseJson()));
            log.warn("Ödeme başarısız: orderReference={}, reason={}",
                    command.orderReference(), result.responseJson());
            eventPublisher.publishPaymentFailed(
                    finalPayment.orderReference(),
                    finalPayment.userId(),
                    result.responseJson()
            );
        }

        return finalPayment;
    }
}
