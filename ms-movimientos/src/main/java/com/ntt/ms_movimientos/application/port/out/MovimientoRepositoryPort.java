package com.ntt.ms_movimientos.application.port.out;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.ntt.ms_movimientos.domain.model.Movimiento;

public interface MovimientoRepositoryPort {

	Movimiento save(Movimiento movimiento);

	Optional<Movimiento> findById(UUID id);

	Optional<Movimiento> findByCommandId(UUID commandId);

	List<Movimiento> findAll();

	List<Movimiento> findByCuentaIdAndFechaBetween(UUID cuentaId, Instant from, Instant to);

	void deleteById(UUID id);
}
