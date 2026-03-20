package com.ntt.ms_cuentas.adapters.in.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.ntt.ms_cuentas.domain.model.TipoCuenta;

public record CuentaResponse(
		UUID id,
		String numeroCuenta,
		TipoCuenta tipo,
		BigDecimal saldoInicial,
		BigDecimal saldoActual,
		boolean estado,
		UUID clienteId,
		Instant createdAt,
		Instant updatedAt
) {
}
