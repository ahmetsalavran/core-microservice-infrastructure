package com.ms.core.infrastructure.outbox.messaging;

public record OutboxJob(
        Long outboxRowId,
        String eventType,
        String keyId,
        String payload
) {
}
