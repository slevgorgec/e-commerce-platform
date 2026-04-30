package com.n11bootcamp.ecommerce.payment.unit;

import com.n11bootcamp.ecommerce.payment.application.dto.InitiatePaymentCommand;
import com.n11bootcamp.ecommerce.payment.application.port.out.EventPublisherPort;
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

    @Mock
    private EventPublisherPort eventPublisher;

    @InjectMocks
    private InitiatePaymentUseCaseImpl useCase;

    @Test
    void execute_givenValidCommand_whenIyzicoSucceeds_createsCompletedPaymentAndPublishesEvent() {
        var orderRef = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var amount = new BigDecimal("299.90");
        var command = new InitiatePaymentCommand(orderRef, userId, amount, "TRY");

        when(paymentRepository.existsByOrderReference(orderRef)).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));
        when(iyzicoGateway.initiateCheckout(orderRef, userId, amount, "TRY"))
                .thenReturn(new IyzicoGatewayPort.IyzicoCheckoutResult(true, "IYZICO-123", "{\"status\":\"success\"}"));

        var result = useCase.execute(command);

        assertThat(result).isNotNull();
        assertThat(result.orderReference()).isEqualTo(orderRef);
        assertThat(result.status()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(result.iyzicoPaymentId()).isEqualTo("IYZICO-123");

        verify(paymentRepository, times(2)).save(any(Payment.class));
        verify(eventPublisher).publishPaymentCompleted(eq(orderRef), eq(userId), eq(amount), eq("IYZICO-123"));
        verify(eventPublisher, never()).publishPaymentFailed(any(), any(), any());
    }

    @Test
    void execute_givenValidCommand_whenIyzicoFails_createsFailedPaymentAndPublishesEvent() {
        var orderRef = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var amount = new BigDecimal("299.90");
        var command = new InitiatePaymentCommand(orderRef, userId, amount, "TRY");

        when(paymentRepository.existsByOrderReference(orderRef)).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));
        when(iyzicoGateway.initiateCheckout(orderRef, userId, amount, "TRY"))
                .thenReturn(new IyzicoGatewayPort.IyzicoCheckoutResult(false, null, "Kart limiti aşıldı"));

        var result = useCase.execute(command);

        assertThat(result.status()).isEqualTo(PaymentStatus.FAILED);
        assertThat(result.iyzicoPaymentId()).isNull();

        verify(eventPublisher).publishPaymentFailed(eq(orderRef), eq(userId), eq("Kart limiti aşıldı"));
        verify(eventPublisher, never()).publishPaymentCompleted(any(), any(), any(), any());
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
        verify(eventPublisher, never()).publishPaymentCompleted(any(), any(), any(), any());
    }
}
