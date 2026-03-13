package com.microservice.infrastructure.idempotency.context;

public enum IdempotencyOutcome {
    PROCEED,
    DUPLICATE
}
