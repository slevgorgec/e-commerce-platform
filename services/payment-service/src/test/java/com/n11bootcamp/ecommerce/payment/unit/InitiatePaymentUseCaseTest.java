package com.n11bootcamp.ecommerce.payment.unit;

import com.n11bootcamp.ecommerce.payment.application.dto.InitiatePaymentCommand;
import com.n11bootcamp.ecommerce.payment.application.port.out.IyzicoGatewayPort;
import com.n11bootcamp.ecommerce.payment.application.port.out.PaymentRepositoryPort;
import com.n11bootcamp.ecommerce.payment.application.usecase.InitiatePaymentUseCaseImpl;
import com.n11bootcamp.ecommerce.payment.domain.exception.DuplicatePaymentException;
import com.n11bootcamp.ecommerce.payment.domain.model.Payment;
import com.n11bootcamp.ecommerce.payment.domain.model.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InitiatePaymentUseCaseTest {

    @Mock
    private PaymentRepositoryPort paymentRepository;

    @Mock
    private IyzicoGatewayPort iyzicoGateway;

    @InjectMocks
    private InitiatePaymentUseCaseImpl useCase;

    @Test
    void execute_givenValidCommand_whenIyzicoSucceeds_storesCheckoutUrl() {
        var orderRef = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var amount = new BigDecimal("299.90");
        var command = new InitiatePaymentCommand(orderRef, userId, amount, "TRY");
        var checkoutUrl = "https://sandbox-cpp.iyzipay.com/checkout/pay/test-token";
        var token = "test-iyzico-token";

        when(paymentRepository.existsByOrderReference(orderRef)).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));
        when(iyzicoGateway.initiateCheckout(orderRef, userId, amount, "TRY"))
                .thenReturn(new IyzicoGatewayPort.IyzicoCheckoutResult(true, checkoutUrl, token, null));

        var result = useCase.execute(command);

        assertThat(result.status()).isEqualTo(PaymentStatus.PENDING);
        assertThat(result.checkoutFormUrl()).isEqualTo(checkoutUrl);
        assertThat(result.iyzicoToken()).isEqualTo(token);
        assertThat(result.iyzicoPaymentId()).isNull();

        verify(paymentRepository, times(2)).save(any(Payment.class));
    }

    @Test
    void execute_givenValidCommand_whenIyzicoFails_savesFailedPayment() {
        var orderRef = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var amount = new BigDecimal("299.90");
        var command = new InitiatePaymentCommand(orderRef, userId, amount, "TRY");

        when(paymentRepository.existsByOrderReference(orderRef)).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));
        when(iyzicoGateway.initiateCheckout(orderRef, userId, amount, "TRY"))
                .thenReturn(new IyzicoGatewayPort.IyzicoCheckoutResult(false, null, null, "Yetersiz bakiye"));

        var result = useCase.execute(command);

        assertThat(result.status()).isEqualTo(PaymentStatus.FAILED);
        assertThat(result.checkoutFormUrl()).isNull();
        assertThat(result.iyzicoToken()).isNull();
    }

    @Test
    void execute_givenDuplicateOrderReference_throwsDuplicatePaymentException() {
        var orderRef = UUID.randomUUID();
        var command = new InitiatePaymentCommand(orderRef, UUID.randomUUID(), new BigDecimal("100.00"), "TRY");

        when(paymentRepository.existsByOrderReference(orderRef)).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(DuplicatePaymentException.class)
                .hasMessageContaining(orderRef.toString());

        verify(paymentRepository, never()).save(any());
        verify(iyzicoGateway, never()).initiateCheckout(any(), any(), any(), any());
    }
}
