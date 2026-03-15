package com.ms.core.infrastructure.idempotency.context;

public enum IdempotencyOutcome {
    PROCEED,
    DUPLICATE
}
