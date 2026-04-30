package com.n11bootcamp.ecommerce.order.application.port.out;

/**
 * İşlenmiş event idempotency port'u (outgoing port).
 * RabbitMQ at-least-once delivery'den kaynaklanan tekrarlı event'leri önler.
 */
public interface ProcessedEventRepositoryPort {

    boolean isAlreadyProcessed(String eventId);

    void markAsProcessed(String eventId);
}