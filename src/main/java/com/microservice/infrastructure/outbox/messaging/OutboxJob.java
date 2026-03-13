package com.microservice.infrastructure.outbox.messaging;

public record OutboxJob(
        Long outboxRowId,
        String eventType,
        String keyId,
        String payload
) {
}
