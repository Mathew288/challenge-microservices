package com.ntt.ms_movimientos.adapters.in.web.mapper;

import org.springframework.stereotype.Component;

import com.ntt.ms_movimientos.adapters.in.web.dto.MovimientoResponse;
import com.ntt.ms_movimientos.domain.model.Movimiento;

@Component
public class MovimientoWebMapper {

	public MovimientoResponse toResponse(Movimiento m) {
		return new MovimientoResponse(
				m.id(),
				m.cuentaId(),
				m.fecha(),
				m.tipo(),
				m.valor(),
				m.saldoDisponible(),
				m.estado(),
				m.sagaId(),
				m.commandId(),
				m.createdAt()
		);
	}
}
