package com.n11bootcamp.ecommerce.product.application.usecase;

import com.n11bootcamp.ecommerce.product.application.dto.ReserveStockCommand;
import com.n11bootcamp.ecommerce.product.application.port.in.ReserveStockUseCase;
import com.n11bootcamp.ecommerce.product.application.port.out.EventPublisherPort;
import com.n11bootcamp.ecommerce.product.application.port.out.ProductVariantRepositoryPort;
import com.n11bootcamp.ecommerce.product.application.port.out.StockReservationRepositoryPort;
import com.n11bootcamp.ecommerce.product.domain.exception.InsufficientStockException;
import com.n11bootcamp.ecommerce.product.domain.exception.VariantNotFoundException;
import com.n11bootcamp.ecommerce.product.domain.model.StockReservation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReserveStockUseCaseImpl implements ReserveStockUseCase {

    private final ProductVariantRepositoryPort variantRepository;
    private final StockReservationRepositoryPort reservationRepository;
    private final EventPublisherPort eventPublisher;

    @Override
    @Transactional
    public void execute(ReserveStockCommand command) {
        var reservations = new ArrayList<StockReservation>();

        try {
            for (var item : command.items()) {
                var variant = variantRepository.findById(item.variantId())
                        .orElseThrow(() -> new VariantNotFoundException(item.variantId()));

                if (variant.availableStock() < item.quantity()) {
                    throw new InsufficientStockException(variant.sku(), variant.availableStock(), item.quantity());
                }

                var updated = variant.reserve(item.quantity());
                variantRepository.save(updated);
                reservations.add(StockReservation.create(command.orderId(), item.variantId(), item.quantity()));
            }

            reservationRepository.saveAll(reservations);
            eventPublisher.publishStockReserved(command.orderId());
            log.info("Stok rezerve edildi: orderId={}", command.orderId());

        } catch (InsufficientStockException | VariantNotFoundException ex) {
            log.warn("Stok rezervasyonu başarısız: orderId={}, reason={}", command.orderId(), ex.getMessage());
            eventPublisher.publishStockReservationFailed(command.orderId(), ex.getMessage());
        }
    }
}
