package com.ntt.ms_movimientos.application.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntt.ms_movimientos.adapters.out.persistence.entity.OutboxMessageEntity;
import com.ntt.ms_movimientos.adapters.out.persistence.repository.SpringDataOutboxMessageRepository;

@Service
public class OutboxService {

	private final SpringDataOutboxMessageRepository outboxRepository;
	private final ObjectMapper objectMapper;

	public OutboxService(SpringDataOutboxMessageRepository outboxRepository, ObjectMapper objectMapper) {
		this.outboxRepository = outboxRepository;
		this.objectMapper = objectMapper;
	}

	@Transactional
	public void enqueue(
			String aggregateType,
			UUID aggregateId,
			String messageType,
			String routingKey,
			String exchange,
			Object payload
	) {
		String json;
		try {
			json = objectMapper.writeValueAsString(payload);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("No se pudo serializar payload para outbox", e);
		}

		OutboxMessageEntity entity = new OutboxMessageEntity(
				UUID.randomUUID(),
				aggregateType,
				aggregateId,
				messageType,
				routingKey,
				exchange,
				json,
				OutboxMessageEntity.Status.PENDING,
				Instant.now(),
				null,
				0,
				null
		);

		outboxRepository.save(entity);
	}
}
