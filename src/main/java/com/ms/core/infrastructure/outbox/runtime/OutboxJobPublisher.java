package com.ms.core.infrastructure.outbox.runtime;

import com.ms.core.infrastructure.outbox.messaging.OutboxJob;

public interface OutboxJobPublisher {
    void publishFastPath(OutboxJob job);
}
