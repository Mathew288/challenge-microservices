package com.ntt.ms_movimientos.adapters.out.persistence.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "outbox_message")
public class OutboxMessageEntity {

	public enum Status {
		PENDING,
		PUBLISHED,
		FAILED
	}

	@Id
	private UUID id;

	@Column(name = "aggregate_type", nullable = false, length = 100)
	private String aggregateType;

	@Column(name = "aggregate_id", nullable = false)
	private UUID aggregateId;

	@Column(name = "message_type", nullable = false, length = 150)
	private String messageType;

	@Column(name = "routing_key", nullable = false, length = 200)
	private String routingKey;

	@Column(name = "exchange", nullable = false, length = 200)
	private String exchange;

	// Nota: mantenemos el JSON como String para portabilidad (H2/Postgres).
	@Column(name = "payload", nullable = false, columnDefinition = "text")
	private String payload;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 30)
	private Status status;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Column(name = "published_at")
	private Instant publishedAt;

	@Column(name = "attempts", nullable = false)
	private int attempts;

	@Column(name = "last_error", columnDefinition = "text")
	private String lastError;

	protected OutboxMessageEntity() {
	}

	public OutboxMessageEntity(
			UUID id,
			String aggregateType,
			UUID aggregateId,
			String messageType,
			String routingKey,
			String exchange,
			String payload,
			Status status,
			Instant createdAt,
			Instant publishedAt,
			int attempts,
			String lastError
	) {
		this.id = id;
		this.aggregateType = aggregateType;
		this.aggregateId = aggregateId;
		this.messageType = messageType;
		this.routingKey = routingKey;
		this.exchange = exchange;
		this.payload = payload;
		this.status = status;
		this.createdAt = createdAt;
		this.publishedAt = publishedAt;
		this.attempts = attempts;
		this.lastError = lastError;
	}

	public UUID getId() {
		return id;
	}

	public String getAggregateType() {
		return aggregateType;
	}

	public UUID getAggregateId() {
		return aggregateId;
	}

	public String getMessageType() {
		return messageType;
	}

	public String getRoutingKey() {
		return routingKey;
	}

	public String getExchange() {
		return exchange;
	}

	public String getPayload() {
		return payload;
	}

	public Status getStatus() {
		return status;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getPublishedAt() {
		return publishedAt;
	}

	public int getAttempts() {
		return attempts;
	}

	public String getLastError() {
		return lastError;
	}

	public void markPublished(Instant when) {
		this.status = Status.PUBLISHED;
		this.publishedAt = when;
	}

	public void markFailed(String error) {
		this.status = Status.FAILED;
		this.lastError = error;
	}

	public void incrementAttempts() {
		this.attempts++;
	}
}
