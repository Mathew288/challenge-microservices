package com.ntt.ms_cuentas.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Cuenta(
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
