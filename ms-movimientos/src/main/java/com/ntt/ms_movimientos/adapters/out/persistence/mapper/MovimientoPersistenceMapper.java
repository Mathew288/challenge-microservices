package com.ntt.ms_movimientos.adapters.out.persistence.mapper;

import org.springframework.stereotype.Component;

import com.ntt.ms_movimientos.adapters.out.persistence.entity.MovimientoEntity;
import com.ntt.ms_movimientos.domain.model.Movimiento;

@Component
public class MovimientoPersistenceMapper {

	public MovimientoEntity toEntity(Movimiento m) {
		MovimientoEntity e = new MovimientoEntity();
		e.setId(m.id());
		e.setCuentaId(m.cuentaId());
		e.setFecha(m.fecha());
		e.setTipo(m.tipo());
		e.setValor(m.valor());
		e.setSaldoDisponible(m.saldoDisponible());
		e.setEstado(m.estado());
		e.setSagaId(m.sagaId());
		e.setCommandId(m.commandId());
		e.setCreatedAt(m.createdAt());
		return e;
	}

	public Movimiento toDomain(MovimientoEntity e) {
		return new Movimiento(
				e.getId(),
				e.getCuentaId(),
				e.getFecha(),
				e.getTipo(),
				e.getValor(),
				e.getSaldoDisponible(),
				e.getEstado(),
				e.getSagaId(),
				e.getCommandId(),
				e.getCreatedAt()
		);
	}
}
