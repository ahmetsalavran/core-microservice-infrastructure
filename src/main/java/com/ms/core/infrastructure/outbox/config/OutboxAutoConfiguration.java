package com.ms.core.infrastructure.outbox.config;

import com.ms.core.infrastructure.outbox.repo.OutboxEventRepository;
import com.ms.core.infrastructure.outbox.runtime.OutboxAfterCommitListener;
import com.ms.core.infrastructure.outbox.runtime.OutboxJobPublisher;
import com.ms.core.infrastructure.outbox.runtime.OutboxPublisherWorker;
import com.ms.core.infrastructure.outbox.runtime.OutboxQueue;
import com.ms.core.infrastructure.outbox.runtime.OutboxStartupLoader;
import com.ms.core.infrastructure.outbox.service.OutboxEventService;
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
