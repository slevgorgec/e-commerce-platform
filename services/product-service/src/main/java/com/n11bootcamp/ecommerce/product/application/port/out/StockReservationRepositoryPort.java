package com.n11bootcamp.ecommerce.product.application.port.out;

import com.n11bootcamp.ecommerce.product.domain.model.ReservationStatus;
import com.n11bootcamp.ecommerce.product.domain.model.StockReservation;

import java.util.List;
import java.util.UUID;

public interface StockReservationRepositoryPort {
    StockReservation save(StockReservation reservation);
    List<StockReservation> findByOrderId(UUID orderId);
    List<StockReservation> findByOrderIdAndStatus(UUID orderId, ReservationStatus status);
    void saveAll(List<StockReservation> reservations);
}
