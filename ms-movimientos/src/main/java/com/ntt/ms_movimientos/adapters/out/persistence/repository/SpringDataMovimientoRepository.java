package com.ntt.ms_movimientos.adapters.out.persistence.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ntt.ms_movimientos.adapters.out.persistence.entity.MovimientoEntity;

public interface SpringDataMovimientoRepository extends JpaRepository<MovimientoEntity, UUID> {

	Optional<MovimientoEntity> findByCommandId(UUID commandId);

	List<MovimientoEntity> findByCuentaIdAndFechaBetweenOrderByFechaAsc(UUID cuentaId, Instant from, Instant to);
}
