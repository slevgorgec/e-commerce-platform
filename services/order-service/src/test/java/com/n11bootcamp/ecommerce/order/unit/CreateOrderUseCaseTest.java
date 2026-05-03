package com.n11bootcamp.ecommerce.order.unit;

import com.n11bootcamp.ecommerce.order.application.dto.CreateOrderCommand;
import com.n11bootcamp.ecommerce.order.application.dto.VariantInfo;
import com.n11bootcamp.ecommerce.order.application.port.out.EventPublisherPort;
import com.n11bootcamp.ecommerce.order.application.port.out.OrderRepositoryPort;
import com.n11bootcamp.ecommerce.order.application.port.out.ProductServicePort;
import com.n11bootcamp.ecommerce.order.application.usecase.CreateOrderUseCaseImpl;
import com.n11bootcamp.ecommerce.order.domain.model.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateOrderUseCaseTest {

    @Mock
    private OrderRepositoryPort orderRepository;

    @Mock
    private EventPublisherPort eventPublisher;

    @Mock
    private ProductServicePort productService;

    @InjectMocks
    private CreateOrderUseCaseImpl useCase;

    @Test
    void execute_givenValidCommand_createsOrderAndPublishesEvent() {
        var productId = UUID.randomUUID();
        var variantId = UUID.randomUUID();
        var items = List.of(new CreateOrderCommand.OrderItemData(productId, variantId, 2));
        var shippingAddress = new CreateOrderCommand.ShippingAddressData(
                "Ahmet Yılmaz", "05321234567",
                "Test Mahallesi, Test Sokak No:1",
                "İstanbul", "Kadıköy", "34700", "Türkiye"
        );
        var command = new CreateOrderCommand(UUID.randomUUID(), items, shippingAddress);

        when(productService.getVariantInfo(variantId)).thenReturn(
                new VariantInfo(productId, variantId, "Test Ürün", "M", new BigDecimal("199.90"), true)
        );
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute(command);

        assertThat(result).isNotNull();
        assertThat(result.items()).hasSize(1);
        assertThat(result.totalAmount()).isEqualByComparingTo(new BigDecimal("399.80"));

        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publishOrderCreated(any(Order.class));
    }

    @Test
    void execute_givenEmptyItems_createsOrderWithZeroTotal() {
        var shippingAddress = new CreateOrderCommand.ShippingAddressData(
                "Ahmet Yılmaz", "05321234567",
                "Test Mahallesi", "İstanbul", "Kadıköy", "34700", "Türkiye"
        );
        var command = new CreateOrderCommand(UUID.randomUUID(), List.of(), shippingAddress);

        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute(command);

        assertThat(result).isNotNull();
        assertThat(result.items()).isEmpty();
        assertThat(result.totalAmount()).isEqualByComparingTo(BigDecimal.ZERO);

        verify(orderRepository).save(any());
        verify(eventPublisher).publishOrderCreated(any());
    }
}
