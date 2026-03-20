package com.ntt.ms_cuentas.adapters.in.web.mapper;

import org.springframework.stereotype.Component;

import com.ntt.ms_cuentas.adapters.in.web.dto.CuentaResponse;
import com.ntt.ms_cuentas.domain.model.Cuenta;

@Component
public class CuentaWebMapper {

	public CuentaResponse toResponse(Cuenta cuenta) {
		if (cuenta == null) {
			return null;
		}
		return new CuentaResponse(
				cuenta.id(),
				cuenta.numeroCuenta(),
				cuenta.tipo(),
				cuenta.saldoInicial(),
				cuenta.saldoActual(),
				cuenta.estado(),
				cuenta.clienteId(),
				cuenta.createdAt(),
				cuenta.updatedAt()
		);
	}
}
