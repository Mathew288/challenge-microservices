package com.ntt.ms_movimientos.application.dto;

import java.time.Instant;
import java.util.UUID;

public record SaldoRechazadoEvent(
		UUID commandId,
		UUID sagaId,
		UUID cuentaId,
		String reason,
		Instant occurredAt
) {
}
