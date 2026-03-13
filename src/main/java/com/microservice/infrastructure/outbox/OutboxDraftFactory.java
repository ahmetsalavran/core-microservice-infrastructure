package com.microservice.infrastructure.outbox;

import java.time.Clock;
import java.time.OffsetDateTime;

public final class OutboxDraftFactory {
    public static final String STATUS_NEW = "NEW";

    private OutboxDraftFactory() {
    }

    public static OutboxDraft newEvent(
            String aggregateType,
            String aggregateId,
            String eventType,
            String key,
            String orderType,
            String payload
    ) {
        return newEvent(aggregateType, aggregateId, eventType, key, orderType, payload, Clock.systemUTC());
    }

    public static OutboxDraft newEvent(
            String aggregateType,
            String aggregateId,
            String eventType,
            String key,
            String orderType,
            String payload,
            Clock clock
    ) {
        OffsetDateTime now = OffsetDateTime.now(clock);
        return new OutboxDraft(
                aggregateType,
                aggregateId,
                eventType,
                key,
                orderType,
                STATUS_NEW,
                0,
                now,
                now,
                payload
        );
    }
}
