package com.microservice.infrastructure.idempotency.config;

import com.microservice.infrastructure.idempotency.aspect.IdempotencyAspect;
import com.microservice.infrastructure.idempotency.repo.ProcessedEventRepository;
import com.microservice.infrastructure.idempotency.support.SpelKeyResolver;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(ProcessedEventRepository.class)
@EnableConfigurationProperties(IdempotencyProperties.class)
@ConditionalOnProperty(prefix = "microservice.idempotency", name = "enabled", havingValue = "true", matchIfMissing = true)
public class IdempotencyAutoConfiguration {

    @Bean
    public SpelKeyResolver spelKeyResolver() {
        return new SpelKeyResolver();
    }

    @Bean
    @ConditionalOnBean(ProcessedEventRepository.class)
    public IdempotencyAspect idempotencyAspect(ProcessedEventRepository repository, SpelKeyResolver spel) {
        return new IdempotencyAspect(repository, spel);
    }
}
