package com.ntt.ms_cuentas.application.port.in;

import com.ntt.ms_cuentas.application.dto.MovimientoCommand;

public interface MovimientoCommandHandler {
	void handle(MovimientoCommand command);
}
