package com.ars.core.infrastructure.idempotency.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {
    String key();
    String eventType() default "";
    String orderId() default "";
    boolean mandatoryTx() default true;
}
