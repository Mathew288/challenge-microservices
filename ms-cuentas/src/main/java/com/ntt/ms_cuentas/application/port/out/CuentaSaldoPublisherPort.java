package com.ntt.ms_cuentas.application.port.out;

import com.ntt.ms_cuentas.application.dto.SaldoActualizadoEvent;
import com.ntt.ms_cuentas.application.dto.SaldoRechazadoEvent;

public interface CuentaSaldoPublisherPort {
	void publishSaldoActualizado(SaldoActualizadoEvent event);

	void publishSaldoRechazado(SaldoRechazadoEvent event);
}
