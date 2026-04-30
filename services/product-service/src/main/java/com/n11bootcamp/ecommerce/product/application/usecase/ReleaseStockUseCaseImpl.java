package com.n11bootcamp.ecommerce.product.application.usecase;

import com.n11bootcamp.ecommerce.product.application.port.in.ReleaseStockUseCase;
import com.n11bootcamp.ecommerce.product.application.port.out.EventPublisherPort;
import com.n11bootcamp.ecommerce.product.application.port.out.ProductVariantRepositoryPort;
import com.n11bootcamp.ecommerce.product.application.port.out.StockReservationRepositoryPort;
import com.n11bootcamp.ecommerce.product.domain.exception.VariantNotFoundException;
import com.n11bootcamp.ecommerce.product.domain.model.ReservationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReleaseStockUseCaseImpl implements ReleaseStockUseCase {

    private final StockReservationRepositoryPort reservationRepository;
    private final ProductVariantRepositoryPort variantRepository;
    private final EventPublisherPort eventPublisher;

    @Override
    @Transactional
    public void execute(UUID orderId) {
        var pendingReservations = reservationRepository.findByOrderIdAndStatus(orderId, ReservationStatus.PENDING);

        var released = pendingReservations.stream().map(reservation -> {
            var variant = variantRepository.findById(reservation.variantId())
                    .orElseThrow(() -> new VariantNotFoundException(reservation.variantId()));
            variantRepository.save(variant.releaseReservation(reservation.quantity()));
            return reservation.release();
        }).toList();

        reservationRepository.saveAll(released);
        eventPublisher.publishStockReleased(orderId);
        log.info("Stok rezervasyonu serbest bırakıldı: orderId={}, rezervasyon sayısı={}", orderId, released.size());
    }
}
