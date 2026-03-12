package com.ars.core.infrastructure.outbox.runtime;

import com.ars.core.infrastructure.outbox.messaging.OutboxJob;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class OutboxQueue {
    private final BlockingQueue<OutboxJob> queue = new LinkedBlockingQueue<>();

    public void enqueue(OutboxJob job) {
        queue.offer(job);
    }

    public OutboxJob take() throws InterruptedException {
        return queue.take();
    }
}
