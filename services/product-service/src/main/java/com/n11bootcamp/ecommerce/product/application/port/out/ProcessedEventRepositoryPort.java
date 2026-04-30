package com.n11bootcamp.ecommerce.product.application.port.out;

public interface ProcessedEventRepositoryPort {
    boolean isAlreadyProcessed(String eventId);
    void markAsProcessed(String eventId);
}
