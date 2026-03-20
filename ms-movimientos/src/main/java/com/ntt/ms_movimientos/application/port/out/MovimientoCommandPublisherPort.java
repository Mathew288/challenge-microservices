package com.ntt.ms_movimientos.application.port.out;

import com.ntt.ms_movimientos.application.dto.MovimientoCommand;

public interface MovimientoCommandPublisherPort {
	void publish(MovimientoCommand command);
}
