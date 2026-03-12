package com.ars.core.infrastructure.idempotency.support;

import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.lang.reflect.Method;

public class SpelKeyResolver {

    private final ExpressionParser parser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    public String resolve(Method method, Object target, Object[] args, String spel) {
        if (spel == null || spel.isBlank()) return "";

        MethodBasedEvaluationContext ctx =
                new MethodBasedEvaluationContext(target, method, args, nameDiscoverer);

        Object val = parser.parseExpression(spel).getValue(ctx);
        return val == null ? "" : String.valueOf(val);
    }
}