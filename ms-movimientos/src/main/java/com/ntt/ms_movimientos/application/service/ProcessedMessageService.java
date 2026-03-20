package com.ntt.ms_movimientos.application.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ntt.ms_movimientos.adapters.out.persistence.entity.ProcessedMessageEntity;
import com.ntt.ms_movimientos.adapters.out.persistence.repository.SpringDataProcessedMessageRepository;

@Service
public class ProcessedMessageService {

	private final SpringDataProcessedMessageRepository repository;

	public ProcessedMessageService(SpringDataProcessedMessageRepository repository) {
		this.repository = repository;
	}

	/**
	 * Marca un mensaje como procesado (idempotencia).
	 *
	 * @return true si se registró por primera vez; false si ya existía.
	 */
	@Transactional
	public boolean markProcessed(String consumer, UUID messageId) {
		try {
			repository.save(new ProcessedMessageEntity(UUID.randomUUID(), messageId, consumer, Instant.now()));
			return true;
		} catch (DataIntegrityViolationException ex) {
			// unique(consumer, messageId) => ya fue procesado
			return false;
		}
	}
}
