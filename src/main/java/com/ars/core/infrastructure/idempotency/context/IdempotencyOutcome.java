package com.ars.core.infrastructure.idempotency.context;

public enum IdempotencyOutcome {
    PROCEED,
    DUPLICATE
}
