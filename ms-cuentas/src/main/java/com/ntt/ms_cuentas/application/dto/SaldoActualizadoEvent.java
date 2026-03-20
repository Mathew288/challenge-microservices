package com.ntt.ms_cuentas.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record SaldoActualizadoEvent(
		UUID commandId,
		UUID sagaId,
		UUID cuentaId,
		BigDecimal saldoDisponible,
		Instant occurredAt
) {
}
