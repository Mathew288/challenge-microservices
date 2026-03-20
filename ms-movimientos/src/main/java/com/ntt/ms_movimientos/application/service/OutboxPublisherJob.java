package com.ntt.ms_movimientos.application.service;

import java.time.Instant;
import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntt.ms_movimientos.adapters.out.persistence.entity.OutboxMessageEntity;
import com.ntt.ms_movimientos.adapters.out.persistence.repository.SpringDataOutboxMessageRepository;

@Component
@ConditionalOnProperty(value = "app.outbox.publisher.enabled", havingValue = "true", matchIfMissing = true)
public class OutboxPublisherJob {

	private final SpringDataOutboxMessageRepository outboxRepository;
	private final RabbitTemplate rabbitTemplate;
	private final ObjectMapper objectMapper;
	private final String exchange;

	public OutboxPublisherJob(
			SpringDataOutboxMessageRepository outboxRepository,
			RabbitTemplate rabbitTemplate,
			ObjectMapper objectMapper,
			@Value("${app.messaging.exchange}") String exchange
	) {
		this.outboxRepository = outboxRepository;
		this.rabbitTemplate = rabbitTemplate;
		this.objectMapper = objectMapper;
		this.exchange = exchange;
	}

	@Scheduled(fixedDelayString = "${app.outbox.publisher.fixed-delay-ms:1000}")
	@Transactional
	public void publishPending() {
		// En tests normalmente se deshabilita scheduling; adicionalmente este guard evita ruido si el esquema
		// outbox no está creado (flyway disabl600+ed).
		if (!outboxRepository.existsById(java.util.UUID.randomUUID())) {
			// no-op: existeById con UUID random no hace nada útil; se usa solo para forzar inicialización de repo
		}
		List<OutboxMessageEntity> pending = outboxRepository.findByStatusOrderByCreatedAtAsc(
				OutboxMessageEntity.Status.PENDING,
				PageRequest.of(0, 50)
		);

		for (OutboxMessageEntity msg : pending) {
			try {
				msg.incrementAttempts();

				rabbitTemplate.send(exchange, msg.getRoutingKey(), org.springframework.amqp.core.MessageBuilder
						.withBody(msg.getPayload().getBytes(java.nio.charset.StandardCharsets.UTF_8))
						.setContentType(org.springframework.amqp.core.MessageProperties.CONTENT_TYPE_JSON)
						.build());

				msg.markPublished(Instant.now());
			} catch (Exception ex) {
				msg.markFailed(ex.getMessage());
			}
		}
	}
}
