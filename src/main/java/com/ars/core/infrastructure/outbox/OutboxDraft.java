package com.ars.core.infrastructure.outbox;

import java.time.OffsetDateTime;

public record OutboxDraft(
        String aggregateType,
        String aggregateId,
        String eventType,
        String key,
        String orderType,
        String status,
        int retries,
        OffsetDateTime availableAt,
        OffsetDateTime createdAt,
        String payload
) {
}
