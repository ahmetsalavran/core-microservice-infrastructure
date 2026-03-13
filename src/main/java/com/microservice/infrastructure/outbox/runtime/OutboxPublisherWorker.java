package com.microservice.infrastructure.outbox.runtime;

import com.microservice.infrastructure.outbox.messaging.OutboxJob;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutboxPublisherWorker {
    private static final Logger log = LoggerFactory.getLogger(OutboxPublisherWorker.class);

    private final OutboxQueue outboxQueue;
    private final OutboxJobPublisher outboxJobPublisher;

    public OutboxPublisherWorker(OutboxQueue outboxQueue, OutboxJobPublisher outboxJobPublisher) {
        this.outboxQueue = outboxQueue;
        this.outboxJobPublisher = outboxJobPublisher;
    }

    @PostConstruct
    public void start() {
        Thread t = new Thread(this::loop, "outbox-publisher");
        t.setDaemon(true);
        t.start();
    }

    private void loop() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                OutboxJob job = outboxQueue.take();
                // TODO(multi-pod): publishFastPath oncesi bu job kaydinin DB'de pod'a
                // ozel sekilde "claim" edildigini garanti edin. Aksi halde farkli podlar
                // ayni outbox satirini publish etmeye calisabilir (duplicate publish).
                outboxJobPublisher.publishFastPath(job);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            } catch (Exception ex) {
                log.warn("Outbox worker hatası. mesaj={}", ex.getMessage());
            }
        }
    }
}
