package com.ntt.ms_movimientos.application.port.in;

import com.ntt.ms_movimientos.application.dto.SaldoActualizadoEvent;
import com.ntt.ms_movimientos.application.dto.SaldoRechazadoEvent;

public interface CuentaSaldoEventHandler {
	void onSaldoActualizado(SaldoActualizadoEvent event);

	void onSaldoRechazado(SaldoRechazadoEvent event);
}
