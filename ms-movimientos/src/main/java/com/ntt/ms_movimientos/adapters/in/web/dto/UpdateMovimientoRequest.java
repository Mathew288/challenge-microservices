package com.ntt.ms_movimientos.adapters.in.web.dto;

import java.math.BigDecimal;

import com.ntt.ms_movimientos.domain.model.TipoMovimiento;

public record UpdateMovimientoRequest(
		TipoMovimiento tipo,
		BigDecimal valor
) {
}
