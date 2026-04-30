package com.n11bootcamp.ecommerce.order.application.port.in;

import java.util.UUID;

/**
 * StockReserved eventi alındığında siparişi STOCK_RESERVED durumuna günceller
 * ve PaymentRequested eventi yayınlar.
 */
public interface HandleStockReservedUseCase {

    void execute(UUID orderId);
}