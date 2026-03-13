package com.microservice.infrastructure.outbox.runtime;

import com.microservice.infrastructure.outbox.messaging.OutboxJob;

public interface OutboxJobPublisher {
    void publishFastPath(OutboxJob job);
}
