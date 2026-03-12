package com.ars.core.infrastructure.idempotency.aspect;

import com.ars.core.infrastructure.idempotency.annotation.Idempotent;
import com.ars.core.infrastructure.idempotency.context.IdempotencyContext;
import com.ars.core.infrastructure.idempotency.context.IdempotencyOutcome;
import com.ars.core.infrastructure.idempotency.entity.ProcessedEvent;
import com.ars.core.infrastructure.idempotency.repo.ProcessedEventRepository;
import com.ars.core.infrastructure.idempotency.support.SpelKeyResolver;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Method;

@Aspect
public class IdempotencyAspect {

    private final ProcessedEventRepository repository;
    private final SpelKeyResolver spel;

    public IdempotencyAspect(ProcessedEventRepository repository, SpelKeyResolver spel) {
        this.repository = repository;
        this.spel = spel;
    }

    //Service Transaction Boundry i bozmuyoruz.
    @Transactional(propagation = Propagation.MANDATORY)
    @Around("@annotation(idempotent)")
    public Object around(ProceedingJoinPoint pjp, Idempotent idempotent) throws Throwable {

        if (idempotent.mandatoryTx() && !TransactionSynchronizationManager.isActualTransactionActive()) {
            throw new IllegalStateException("@Idempotent aktif bir transaction gerektirir (mandatoryTx=true).");
        }

        Method signatureMethod = ((MethodSignature) pjp.getSignature()).getMethod();
        Method targetMethod = AopUtils.getMostSpecificMethod(signatureMethod, pjp.getTarget().getClass());
        targetMethod = BridgeMethodResolver.findBridgedMethod(targetMethod);

        String eventId = spel.resolve(targetMethod, pjp.getTarget(), pjp.getArgs(), idempotent.key());
        if (eventId == null || eventId.isBlank()) {
            throw new IllegalStateException("Idempotency key boş çözümlendi. @Idempotent(key=...) kontrol edin.");
        }

        String eventType = spel.resolve(targetMethod, pjp.getTarget(), pjp.getArgs(), idempotent.eventType());
        if (eventType == null || eventType.isBlank()) eventType = "UNKNOWN";

        String orderIdStr = spel.resolve(targetMethod, pjp.getTarget(), pjp.getArgs(), idempotent.orderId());
        Long orderId = 0L;
        if (orderIdStr != null && !orderIdStr.isBlank()) {
            try { orderId = Long.valueOf(orderIdStr); } catch (NumberFormatException ignored) {}
        }

        int inserted = repository.tryInsert(eventId, eventType, orderId, "PROCESSING");

        if (inserted == 1) {
            IdempotencyContext.set(IdempotencyOutcome.PROCEED);
        } else {
            IdempotencyContext.set(IdempotencyOutcome.DUPLICATE);
        }

        try {
            return pjp.proceed();
        } finally {
            IdempotencyContext.clear();
        }
    }
}
