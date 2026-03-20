package com.ntt.ms_cuentas.adapters.in.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.ntt.ms_cuentas.domain.model.TipoCuenta;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCuentaRequest(
		@NotBlank(message = "numeroCuenta es requerido")
		@Size(max = 20, message = "numeroCuenta debe tener máximo 20 caracteres")
		String numeroCuenta,

		@NotNull(message = "tipo es requerido")
		TipoCuenta tipo,

		@NotNull(message = "saldoInicial es requerido")
		@DecimalMin(value = "0.0", inclusive = true, message = "saldoInicial debe ser >= 0")
		BigDecimal saldoInicial,

		@NotNull(message = "estado es requerido")
		Boolean estado,

		@NotNull(message = "clienteId es requerido")
		UUID clienteId
) {
}
