package com.ntt.ms_cuentas.adapters.out.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ntt.ms_cuentas.application.dto.SaldoActualizadoEvent;
import com.ntt.ms_cuentas.application.dto.SaldoRechazadoEvent;
import com.ntt.ms_cuentas.application.port.out.CuentaSaldoPublisherPort;

@Component
public class CuentaSaldoRabbitPublisher implements CuentaSaldoPublisherPort {

	private final RabbitTemplate rabbitTemplate;
	private final String exchange;
	private final String rkSaldoActualizado;
	private final String rkSaldoRechazado;

	public CuentaSaldoRabbitPublisher(
			RabbitTemplate rabbitTemplate,
			@Value("${app.messaging.exchange}") String exchange,
			@Value("${app.messaging.routing-keys.saldo-actualizado}") String rkSaldoActualizado,
			@Value("${app.messaging.routing-keys.saldo-rechazado}") String rkSaldoRechazado
	) {
		this.rabbitTemplate = rabbitTemplate;
		this.exchange = exchange;
		this.rkSaldoActualizado = rkSaldoActualizado;
		this.rkSaldoRechazado = rkSaldoRechazado;
	}

	@Override
	public void publishSaldoActualizado(SaldoActualizadoEvent event) {
		rabbitTemplate.convertAndSend(exchange, rkSaldoActualizado, event);
	}

	@Override
	public void publishSaldoRechazado(SaldoRechazadoEvent event) {
		rabbitTemplate.convertAndSend(exchange, rkSaldoRechazado, event);
	}
}
