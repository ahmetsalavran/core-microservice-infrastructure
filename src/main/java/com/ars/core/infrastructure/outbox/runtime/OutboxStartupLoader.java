package com.ars.core.infrastructure.outbox.runtime;

import com.ars.core.infrastructure.outbox.entity.OutboxEvent;
import com.ars.core.infrastructure.outbox.messaging.OutboxJob;
import com.ars.core.infrastructure.outbox.repo.OutboxEventRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.List;

public class OutboxStartupLoader {
    private final OutboxEventRepository outboxEventRepository;
    private final OutboxQueue outboxQueue;

    public OutboxStartupLoader(OutboxEventRepository outboxEventRepository, OutboxQueue outboxQueue) {
        this.outboxEventRepository = outboxEventRepository;
        this.outboxQueue = outboxQueue;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void load() {
        // TODO(multi-pod): Bu toplu yukleme multi-pod ortaminda ayni NEW/FAILED kayitlarin
        // birden fazla pod tarafindan queue'ya alinmasina yol acabilir. Burayi
        // "claim" mantigina tasiyin (or. FOR UPDATE SKIP LOCKED + NEW->PROCESSING),
        // veya distributed lock ile tek pod startup yuklemesi yapin.
        List<OutboxEvent> events = outboxEventRepository.findByStatusInOrderByIdAsc(List.of("NEW", "FAILED"));
        events.forEach(event -> outboxQueue.enqueue(new OutboxJob(
                event.getId(),
                event.getEventType(),
                event.getKey(),
                event.getPayload()
        )));
    }
}
