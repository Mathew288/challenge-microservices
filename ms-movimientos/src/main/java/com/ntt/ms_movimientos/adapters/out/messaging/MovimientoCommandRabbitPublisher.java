package com.ntt.ms_movimientos.adapters.out.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ntt.ms_movimientos.application.dto.MovimientoCommand;
import com.ntt.ms_movimientos.application.port.out.MovimientoCommandPublisherPort;

@Component
public class MovimientoCommandRabbitPublisher implements MovimientoCommandPublisherPort {

	private final RabbitTemplate rabbitTemplate;
	private final String exchange;
	private final String rkDebitoSolicitado;
	private final String rkCreditoSolicitado;

	public MovimientoCommandRabbitPublisher(
			RabbitTemplate rabbitTemplate,
			@Value("${app.messaging.exchange}") String exchange,
			@Value("${app.messaging.routing-keys.debito-solicitado}") String rkDebitoSolicitado,
			@Value("${app.messaging.routing-keys.credito-solicitado}") String rkCreditoSolicitado
	) {
		this.rabbitTemplate = rabbitTemplate;
		this.exchange = exchange;
		this.rkDebitoSolicitado = rkDebitoSolicitado;
		this.rkCreditoSolicitado = rkCreditoSolicitado;
	}

	@Override
	public void publish(MovimientoCommand command) {
		String routingKey = command.tipoMovimiento().name().equalsIgnoreCase("DEBITO")
				? rkDebitoSolicitado
				: rkCreditoSolicitado;

		rabbitTemplate.convertAndSend(exchange, routingKey, command);
	}
}
