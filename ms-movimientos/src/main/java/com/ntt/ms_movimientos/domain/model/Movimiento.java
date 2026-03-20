package com.ntt.ms_movimientos.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Movimiento(
		UUID id,
		UUID cuentaId,
		Instant fecha,
		TipoMovimiento tipo,
		BigDecimal valor,
		BigDecimal saldoDisponible,
		EstadoMovimiento estado,
		UUID sagaId,
		UUID commandId,
		Instant createdAt
) {
}
