package com.ntt.ms_movimientos.adapters.in.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.ntt.ms_movimientos.domain.model.EstadoMovimiento;
import com.ntt.ms_movimientos.domain.model.TipoMovimiento;

public record MovimientoResponse(
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
