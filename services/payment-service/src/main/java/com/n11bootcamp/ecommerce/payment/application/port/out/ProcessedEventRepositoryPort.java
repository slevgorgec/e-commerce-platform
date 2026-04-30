package com.n11bootcamp.ecommerce.payment.application.port.out;

public interface ProcessedEventRepositoryPort {

    boolean isAlreadyProcessed(String eventId);

    void markAsProcessed(String eventId);
}