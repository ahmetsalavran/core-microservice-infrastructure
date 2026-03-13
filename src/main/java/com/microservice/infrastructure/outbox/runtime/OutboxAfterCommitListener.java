package com.microservice.infrastructure.outbox.runtime;

import com.microservice.infrastructure.outbox.messaging.OutboxCreatedEvent;
import com.microservice.infrastructure.outbox.messaging.OutboxJob;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

public class OutboxAfterCommitListener {
    private final OutboxQueue outboxQueue;

    public OutboxAfterCommitListener(OutboxQueue outboxQueue) {
        this.outboxQueue = outboxQueue;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOutboxCreated(OutboxCreatedEvent event) {
        outboxQueue.enqueue(new OutboxJob(
                event.outboxRowId(),
                event.eventType(),
                event.key(),
                event.payload()
        ));
    }
}
