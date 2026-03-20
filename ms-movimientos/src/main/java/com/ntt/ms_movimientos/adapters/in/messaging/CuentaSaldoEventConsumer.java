package com.ntt.ms_movimientos.adapters.in.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ntt.ms_movimientos.application.dto.SaldoActualizadoEvent;
import com.ntt.ms_movimientos.application.dto.SaldoRechazadoEvent;
import com.ntt.ms_movimientos.application.port.in.CuentaSaldoEventHandler;

@Component
public class CuentaSaldoEventConsumer {

	private final CuentaSaldoEventHandler handler;

	public CuentaSaldoEventConsumer(CuentaSaldoEventHandler handler) {
		this.handler = handler;
	}

	@RabbitListener(queues = "${app.messaging.queues.cuentas}")
	public void onSaldoActualizado(SaldoActualizadoEvent event) {
		handler.onSaldoActualizado(event);
	}

	@RabbitListener(queues = "${app.messaging.queues.cuentas}")
	public void onSaldoRechazado(SaldoRechazadoEvent event) {
		handler.onSaldoRechazado(event);
	}
}
