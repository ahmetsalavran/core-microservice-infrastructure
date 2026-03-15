package com.ms.core.infrastructure.outbox.messaging;

public record OutboxCreatedEvent(
        Long outboxRowId,
        String eventType,
        String key,
        String payload
) {
}
