package com.ntt.ms_cuentas.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

	@Bean
	public Declarables rabbitDeclarables(
			@Value("${app.messaging.exchange}") String exchangeName,
			@Value("${app.messaging.queues.movimientos-commands}") String movimientosCommandsQueue,
			@Value("${app.messaging.routing-keys.debito-solicitado}") String rkDebitoSolicitado,
			@Value("${app.messaging.routing-keys.credito-solicitado}") String rkCreditoSolicitado
	) {
		TopicExchange exchange = new TopicExchange(exchangeName, true, false);

		Queue queue = new Queue(movimientosCommandsQueue, true);
		Binding b1 = BindingBuilder.bind(queue).to(exchange).with(rkDebitoSolicitado);
		Binding b2 = BindingBuilder.bind(queue).to(exchange).with(rkCreditoSolicitado);

		return new Declarables(exchange, queue, b1, b2);
	}
}
