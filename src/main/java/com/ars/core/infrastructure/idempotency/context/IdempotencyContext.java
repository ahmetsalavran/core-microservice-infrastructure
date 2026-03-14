package com.ars.core.infrastructure.idempotency.context;

public final class IdempotencyContext {

    private static final ThreadLocal<IdempotencyOutcome> OUTCOME = new ThreadLocal<>();

    private IdempotencyContext() {}

    public static void set(IdempotencyOutcome outcome) { OUTCOME.set(outcome); }
    public static IdempotencyOutcome get() { return OUTCOME.get(); }

    public static boolean isDuplicate() { return OUTCOME.get() == IdempotencyOutcome.DUPLICATE; }
    public static boolean isProceed() { return OUTCOME.get() == IdempotencyOutcome.PROCEED; }

    public static void clear() { OUTCOME.remove(); }
}
