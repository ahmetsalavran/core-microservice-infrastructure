package com.microservice.infrastructure.idempotency.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "processed_event")
public class ProcessedEvent {

    @Id
    @Column(name = "event_id", length = 80, nullable = false)
    private String eventId;

    @Column(name = "event_type", length = 40, nullable = false)
    private String eventType;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "status", length = 200, nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected ProcessedEvent() {}

    public ProcessedEvent(String eventId, String eventType, Long orderId, String status) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.orderId = orderId;
        this.status = status;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public String getEventId() { return eventId; }
    public String getEventType() { return eventType; }
    public Long getOrderId() { return orderId; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void setStatus(String status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }
}
