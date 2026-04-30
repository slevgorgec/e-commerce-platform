package com.n11bootcamp.ecommerce.product.unit;

import com.n11bootcamp.ecommerce.product.application.dto.ReserveStockCommand;
import com.n11bootcamp.ecommerce.product.application.port.out.EventPublisherPort;
import com.n11bootcamp.ecommerce.product.application.port.out.ProductVariantRepositoryPort;
import com.n11bootcamp.ecommerce.product.application.port.out.StockReservationRepositoryPort;
import com.n11bootcamp.ecommerce.product.application.usecase.ReserveStockUseCaseImpl;
import com.n11bootcamp.ecommerce.product.domain.model.ProductVariant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReserveStockUseCaseTest {

    @Mock
    private ProductVariantRepositoryPort variantRepository;

    @Mock
    private StockReservationRepositoryPort reservationRepository;

    @Mock
    private EventPublisherPort eventPublisher;

    @InjectMocks
    private ReserveStockUseCaseImpl useCase;

    @Test
    void execute_givenSufficientStock_reservesAndPublishesSuccess() {
        var variantId = UUID.randomUUID();
        var orderId = UUID.randomUUID();
        var variant = new ProductVariant(variantId, UUID.randomUUID(), "SKU-001", "M",
                new BigDecimal("99.90"), 10, 0);

        when(variantRepository.findById(variantId)).thenReturn(Optional.of(variant));
        when(variantRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var command = new ReserveStockCommand(orderId, List.of(new ReserveStockCommand.Item(variantId, 3)));
        useCase.execute(command);

        verify(variantRepository).save(any());
        verify(reservationRepository).saveAll(any());
        verify(eventPublisher).publishStockReserved(orderId);
        verify(eventPublisher, never()).publishStockReservationFailed(any(), any());
    }

    @Test
    void execute_givenInsufficientStock_publishesFailure() {
        var variantId = UUID.randomUUID();
        var orderId = UUID.randomUUID();
        var variant = new ProductVariant(variantId, UUID.randomUUID(), "SKU-002", "L",
                new BigDecimal("99.90"), 2, 0);

        when(variantRepository.findById(variantId)).thenReturn(Optional.of(variant));

        var command = new ReserveStockCommand(orderId, List.of(new ReserveStockCommand.Item(variantId, 5)));
        useCase.execute(command);

        verify(eventPublisher, never()).publishStockReserved(any());
        verify(eventPublisher).publishStockReservationFailed(eq(orderId), any());
    }
}
