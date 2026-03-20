package com.ntt.ms_cuentas.adapters.in.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ntt.ms_cuentas.application.dto.MovimientoCommand;
import com.ntt.ms_cuentas.application.port.in.MovimientoCommandHandler;

@Component
public class MovimientoCommandConsumer {

	private final MovimientoCommandHandler handler;

	public MovimientoCommandConsumer(MovimientoCommandHandler handler) {
		this.handler = handler;
	}

	@RabbitListener(queues = "${app.messaging.queues.movimientos-commands}")
	public void onCommand(MovimientoCommand command) {
		handler.handle(command);
	}
}
