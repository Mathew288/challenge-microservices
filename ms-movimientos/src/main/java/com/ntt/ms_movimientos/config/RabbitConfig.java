package com.ntt.ms_movimientos.config;

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
			@Value("${app.messaging.queues.cuentas}") String cuentasQueue,
			@Value("${app.messaging.routing-keys.saldo-actualizado}") String rkSaldoActualizado,
			@Value("${app.messaging.routing-keys.saldo-rechazado}") String rkSaldoRechazado
	) {
		TopicExchange exchange = new TopicExchange(exchangeName, true, false);

		Queue queue = new Queue(cuentasQueue, true);
		Binding b1 = BindingBuilder.bind(queue).to(exchange).with(rkSaldoActualizado);
		Binding b2 = BindingBuilder.bind(queue).to(exchange).with(rkSaldoRechazado);

		return new Declarables(exchange, queue, b1, b2);
	}
}
