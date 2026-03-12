package com.ars.core.infrastructure.outbox.runtime;

import com.ars.core.infrastructure.outbox.messaging.OutboxJob;

public interface OutboxJobPublisher {
    void publishFastPath(OutboxJob job);
}
