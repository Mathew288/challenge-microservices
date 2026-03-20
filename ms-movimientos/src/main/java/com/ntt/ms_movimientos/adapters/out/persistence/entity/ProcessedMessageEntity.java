package com.ntt.ms_movimientos.adapters.out.persistence.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "processed_message")
public class ProcessedMessageEntity {

	@Id
	private UUID id;

	@Column(name = "message_id", nullable = false)
	private UUID messageId;

	@Column(name = "consumer", nullable = false, length = 200)
	private String consumer;

	@Column(name = "processed_at", nullable = false)
	private Instant processedAt;

	protected ProcessedMessageEntity() {
	}

	public ProcessedMessageEntity(UUID id, UUID messageId, String consumer, Instant processedAt) {
		this.id = id;
		this.messageId = messageId;
		this.consumer = consumer;
		this.processedAt = processedAt;
	}

	public UUID getId() {
		return id;
	}

	public UUID getMessageId() {
		return messageId;
	}

	public String getConsumer() {
		return consumer;
	}

	public Instant getProcessedAt() {
		return processedAt;
	}
}
