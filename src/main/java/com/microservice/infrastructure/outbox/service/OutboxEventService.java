package com.microservice.infrastructure.outbox.service;

import com.microservice.infrastructure.outbox.OutboxDraft;
import com.microservice.infrastructure.outbox.OutboxDraftFactory;
import com.microservice.infrastructure.outbox.entity.OutboxEvent;
import com.microservice.infrastructure.outbox.repo.OutboxEventRepository;

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
