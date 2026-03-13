package com.microservice.infrastructure.outbox.config;

import com.microservice.infrastructure.outbox.repo.OutboxEventRepository;
import com.microservice.infrastructure.outbox.runtime.OutboxAfterCommitListener;
import com.microservice.infrastructure.outbox.runtime.OutboxJobPublisher;
import com.microservice.infrastructure.outbox.runtime.OutboxPublisherWorker;
import com.microservice.infrastructure.outbox.runtime.OutboxQueue;
import com.microservice.infrastructure.outbox.runtime.OutboxStartupLoader;
import com.microservice.infrastructure.outbox.service.OutboxEventService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(OutboxEventRepository.class)
public class OutboxAutoConfiguration {

    @Bean
    @ConditionalOnBean(OutboxEventRepository.class)
    public OutboxEventService outboxEventService(OutboxEventRepository repository) {
        return new OutboxEventService(repository);
    }

    @Bean
    @ConditionalOnBean(OutboxJobPublisher.class)
    public OutboxQueue outboxQueue() {
        return new OutboxQueue();
    }

    @Bean
    @ConditionalOnBean({OutboxJobPublisher.class, OutboxQueue.class})
    public OutboxAfterCommitListener outboxAfterCommitListener(OutboxQueue outboxQueue) {
        return new OutboxAfterCommitListener(outboxQueue);
    }

    @Bean
    @ConditionalOnBean({OutboxJobPublisher.class, OutboxQueue.class, OutboxEventRepository.class})
    public OutboxStartupLoader outboxStartupLoader(OutboxEventRepository outboxEventRepository, OutboxQueue outboxQueue) {
        return new OutboxStartupLoader(outboxEventRepository, outboxQueue);
    }

    @Bean
    @ConditionalOnBean({OutboxJobPublisher.class, OutboxQueue.class})
    public OutboxPublisherWorker outboxPublisherWorker(OutboxQueue outboxQueue, OutboxJobPublisher outboxJobPublisher) {
        return new OutboxPublisherWorker(outboxQueue, outboxJobPublisher);
    }
}
