package com.ntt.ms_movimientos.adapters.in.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ReporteEstadoCuentaResponse(
		UUID clienteId,
		ClienteReporteItem cliente,
		Instant from,
		Instant to,
		List<CuentaReporteItem> cuentas,
		BigDecimal totalCreditos,
		BigDecimal totalDebitos,
		String pdfBase64
) {

	public record ClienteReporteItem(
			UUID id,
			String identificacion,
			String nombre,
			String genero,
			Integer edad,
			String direccion,
			String telefono,
			Boolean estado
	) {
	}

	public record CuentaReporteItem(
			UUID cuentaId,
			String numeroCuenta,
			String tipoCuenta,
			BigDecimal saldoInicial,
			BigDecimal saldoActual,
			List<MovimientoReporteItem> movimientos,
			BigDecimal totalCreditos,
			BigDecimal totalDebitos
	) {
	}

	public record MovimientoReporteItem(
			UUID movimientoId,
			UUID cuentaId,
			String numeroCuenta,
			String tipoCuenta,
			Instant fecha,
			String tipoMovimiento,
			BigDecimal valor,
			BigDecimal saldoDisponible,
			String estado,
			UUID sagaId,
			UUID commandId,
			Instant createdAt
	) {
	}
}
