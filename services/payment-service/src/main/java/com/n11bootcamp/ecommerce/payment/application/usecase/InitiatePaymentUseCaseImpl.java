package com.n11bootcamp.ecommerce.payment.application.usecase;

import com.n11bootcamp.ecommerce.payment.application.dto.InitiatePaymentCommand;
import com.n11bootcamp.ecommerce.payment.application.port.in.InitiatePaymentUseCase;
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

        if (!result.success()) {
            log.error("Iyzico checkout formu oluşturulamadı: orderReference={}, error={}",
                    command.orderReference(), result.errorMessage());
            // Ödeme kaydını başarısız olarak işaretle — callback bekleme
            return paymentRepository.save(saved.withFailed(result.errorMessage()));
        }

        log.info("Checkout formu oluşturuldu, kullanıcı yönlendirme bekleniyor: orderReference={}",
                command.orderReference());
        return paymentRepository.save(saved.withCheckoutInitiated(result.checkoutFormUrl(), result.iyzicoToken()));
    }
}
