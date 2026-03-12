package com.ars.core.infrastructure.outbox.service;

import com.ars.core.infrastructure.outbox.OutboxDraft;
import com.ars.core.infrastructure.outbox.OutboxDraftFactory;
import com.ars.core.infrastructure.outbox.entity.OutboxEvent;
import com.ars.core.infrastructure.outbox.repo.OutboxEventRepository;

public class OutboxEventService {

    private final OutboxEventRepository repository;

    public OutboxEventService(OutboxEventRepository repository) {
        this.repository = repository;
    }

    public OutboxEvent createAndSave(
            String aggregateType,
            String aggregateId,
            String eventType,
            String key,
            String orderType,
            String payload
    ) {
        OutboxDraft draft = OutboxDraftFactory.newEvent(
                aggregateType,
                aggregateId,
                eventType,
                key,
                orderType,
                payload
        );
        return repository.save(OutboxEvent.fromDraft(draft));
    }
}
