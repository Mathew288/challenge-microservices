package com.ntt.ms_movimientos.adapters.in.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.ntt.ms_movimientos.domain.model.TipoMovimiento;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record CreateMovimientoRequest(
		@NotNull(message = "cuentaId es requerido")
		UUID cuentaId,

		@NotNull(message = "tipo es requerido")
		TipoMovimiento tipo,

		@NotNull(message = "valor es requerido")
		@DecimalMin(value = "0.01", message = "valor debe ser mayor a 0")
		BigDecimal valor
) {
}
