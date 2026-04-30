package com.n11bootcamp.ecommerce.product.application.usecase;

import com.n11bootcamp.ecommerce.product.application.port.in.CommitStockUseCase;
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
public class CommitStockUseCaseImpl implements CommitStockUseCase {

    private final StockReservationRepositoryPort reservationRepository;
    private final ProductVariantRepositoryPort variantRepository;
    private final EventPublisherPort eventPublisher;

    @Override
    @Transactional
    public void execute(UUID orderId) {
        var pendingReservations = reservationRepository.findByOrderIdAndStatus(orderId, ReservationStatus.PENDING);

        var committed = pendingReservations.stream().map(reservation -> {
            var variant = variantRepository.findById(reservation.variantId())
                    .orElseThrow(() -> new VariantNotFoundException(reservation.variantId()));
            variantRepository.save(variant.commitReservation(reservation.quantity()));
            return reservation.commit();
        }).toList();

        reservationRepository.saveAll(committed);
        eventPublisher.publishStockCommitted(orderId);
        log.info("Stok taahhüt edildi: orderId={}, rezervasyon sayısı={}", orderId, committed.size());
    }
}
