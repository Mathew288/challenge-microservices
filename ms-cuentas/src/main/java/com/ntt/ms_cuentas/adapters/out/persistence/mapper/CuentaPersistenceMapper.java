package com.ntt.ms_cuentas.adapters.out.persistence.mapper;

import org.springframework.stereotype.Component;

import com.ntt.ms_cuentas.adapters.out.persistence.entity.CuentaEntity;
import com.ntt.ms_cuentas.domain.model.Cuenta;

@Component
public class CuentaPersistenceMapper {

	public Cuenta toDomain(CuentaEntity e) {
		if (e == null) {
			return null;
		}
		return new Cuenta(
				e.getId(),
				e.getNumeroCuenta(),
				e.getTipo(),
				e.getSaldoInicial(),
				e.getSaldoActual(),
				e.isEstado(),
				e.getClienteId(),
				e.getCreatedAt(),
				e.getUpdatedAt()
		);
	}

	public CuentaEntity toEntity(Cuenta d) {
		if (d == null) {
			return null;
		}
		CuentaEntity e = new CuentaEntity();
		e.setId(d.id());
		e.setNumeroCuenta(d.numeroCuenta());
		e.setTipo(d.tipo());
		e.setSaldoInicial(d.saldoInicial());
		e.setSaldoActual(d.saldoActual());
		e.setEstado(d.estado());
		e.setClienteId(d.clienteId());
		e.setCreatedAt(d.createdAt());
		e.setUpdatedAt(d.updatedAt());
		return e;
	}
}
