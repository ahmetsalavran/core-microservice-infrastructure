package com.ars.core.infrastructure.outbox.entity;

import com.ars.core.infrastructure.outbox.OutboxDraft;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "outbox_event")
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "aggregate_type", nullable = false)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false)
    private String aggregateId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "key", nullable = false)
    private String key;

    @Column(name = "order_type", nullable = false)
    private String orderType;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "retries", nullable = false)
    private int retries;

    @Column(name = "available_at", nullable = false)
    private OffsetDateTime availableAt;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "sent_at")
    private OffsetDateTime sentAt;

    @Column(name = "payload", nullable = false)
    private String payload;

    public static OutboxEvent fromDraft(OutboxDraft draft) {
        OutboxEvent outbox = new OutboxEvent();
        outbox.setAggregateType(draft.aggregateType());
        outbox.setAggregateId(draft.aggregateId());
        outbox.setEventType(draft.eventType());
        outbox.setKey(draft.key());
        outbox.setOrderType(draft.orderType());
        outbox.setStatus(draft.status());
        outbox.setRetries(draft.retries());
        outbox.setAvailableAt(draft.availableAt());
        outbox.setCreatedAt(draft.createdAt());
        outbox.setPayload(draft.payload());
        return outbox;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public void setAggregateType(String aggregateType) {
        this.aggregateType = aggregateType;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public void setAggregateId(String aggregateId) {
        this.aggregateId = aggregateId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public OffsetDateTime getAvailableAt() {
        return availableAt;
    }

    public void setAvailableAt(OffsetDateTime availableAt) {
        this.availableAt = availableAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(OffsetDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
