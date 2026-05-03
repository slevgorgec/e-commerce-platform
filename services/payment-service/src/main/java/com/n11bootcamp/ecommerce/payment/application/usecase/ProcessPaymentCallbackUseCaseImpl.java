package com.n11bootcamp.ecommerce.payment.application.usecase;

import com.n11bootcamp.ecommerce.payment.application.port.in.ProcessPaymentCallbackUseCase;
import com.n11bootcamp.ecommerce.payment.application.port.out.EventPublisherPort;
import com.n11bootcamp.ecommerce.payment.application.port.out.IyzicoGatewayPort;
import com.n11bootcamp.ecommerce.payment.application.port.out.PaymentRepositoryPort;
import com.n11bootcamp.ecommerce.payment.domain.exception.PaymentNotFoundException;
import com.n11bootcamp.ecommerce.payment.domain.model.Payment;
import com.n11bootcamp.ecommerce.payment.domain.model.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessPaymentCallbackUseCaseImpl implements ProcessPaymentCallbackUseCase {

    private final PaymentRepositoryPort paymentRepository;
    private final IyzicoGatewayPort iyzicoGateway;
    private final EventPublisherPort eventPublisher;

    @Override
    @Transactional
    public Payment execute(String iyzicoToken) {
        log.info("Iyzico callback işleniyor: token={}", iyzicoToken);

        var payment = paymentRepository.findByIyzicoToken(iyzicoToken)
                .orElseThrow(() -> {
                    log.error("Token ile ödeme bulunamadı: token={}", iyzicoToken);
                    return new PaymentNotFoundException(null);
                });

        // Idempotency: zaten tamamlanmış/başarısızsa tekrar işleme
        if (payment.status() != PaymentStatus.PENDING) {
            log.warn("Ödeme zaten işlenmiş: orderReference={}, status={}",
                    payment.orderReference(), payment.status());
            return payment;
        }

        var result = iyzicoGateway.retrievePaymentResult(iyzicoToken);

        Payment updated;
        if (result.success()) {
            updated = paymentRepository.save(payment.withCompleted(result.paymentId(), result.responseJson()));
            log.info("Ödeme başarılı, event yayınlanıyor: orderReference={}", payment.orderReference());
            eventPublisher.publishPaymentCompleted(
                    updated.orderReference(),
                    updated.userId(),
                    updated.amount(),
                    result.paymentId()
            );
        } else {
            updated = paymentRepository.save(payment.withFailed(result.failureReason()));
            log.warn("Ödeme başarısız, event yayınlanıyor: orderReference={}, reason={}",
                    payment.orderReference(), result.failureReason());
            eventPublisher.publishPaymentFailed(
                    updated.orderReference(),
                    updated.userId(),
                    result.failureReason()
            );
        }

        return updated;
    }
}
