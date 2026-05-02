package com.n11bootcamp.ecommerce.product.unit;

import com.n11bootcamp.ecommerce.product.application.dto.CreateProductCommand;
import com.n11bootcamp.ecommerce.product.application.port.out.ProductRepositoryPort;
import com.n11bootcamp.ecommerce.product.application.usecase.CreateProductUseCaseImpl;
import com.n11bootcamp.ecommerce.product.domain.exception.SlugAlreadyExistsException;
import com.n11bootcamp.ecommerce.product.domain.model.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateProductUseCaseTest {

    @Mock
    private ProductRepositoryPort productRepository;

    @InjectMocks
    private CreateProductUseCaseImpl useCase;

    @Test
    void execute_givenValidCommand_createsProduct() {
        var command = new CreateProductCommand("Test Ürün", "test-urun", "Açıklama",
                UUID.randomUUID(), new BigDecimal("99.90"), null);

        when(productRepository.existsBySlug(command.slug())).thenReturn(false);
        when(productRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute(command);

        assertThat(result.name()).isEqualTo("Test Ürün");
        assertThat(result.slug()).isEqualTo("test-urun");
        assertThat(result.active()).isTrue();
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void execute_givenDuplicateSlug_throwsSlugAlreadyExistsException() {
        var command = new CreateProductCommand("Test", "var-olan-slug", null,
                null, new BigDecimal("50.00"), null);

        when(productRepository.existsBySlug("var-olan-slug")).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(SlugAlreadyExistsException.class);

        verify(productRepository, never()).save(any());
    }
}
