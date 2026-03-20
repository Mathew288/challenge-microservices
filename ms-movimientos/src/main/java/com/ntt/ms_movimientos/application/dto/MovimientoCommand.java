package com.ntt.ms_movimientos.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.ntt.ms_movimientos.domain.model.TipoMovimiento;

public record MovimientoCommand(
		UUID commandId,
		UUID sagaId,
		UUID cuentaId,
		TipoMovimiento tipoMovimiento,
		BigDecimal valor,
		Instant occurredAt
) {
}
