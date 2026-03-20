package com.ntt.ms_movimientos.adapters.out.persistence;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ntt.ms_movimientos.adapters.out.persistence.entity.MovimientoEntity;
import com.ntt.ms_movimientos.adapters.out.persistence.mapper.MovimientoPersistenceMapper;
import com.ntt.ms_movimientos.adapters.out.persistence.repository.SpringDataMovimientoRepository;
import com.ntt.ms_movimientos.application.port.out.MovimientoRepositoryPort;
import com.ntt.ms_movimientos.domain.model.Movimiento;

@Component
public class MovimientoJpaAdapter implements MovimientoRepositoryPort {

	private final SpringDataMovimientoRepository repository;
	private final MovimientoPersistenceMapper mapper;

	public MovimientoJpaAdapter(SpringDataMovimientoRepository repository, MovimientoPersistenceMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	@Override
	@Transactional
	public Movimiento save(Movimiento movimiento) {
		MovimientoEntity saved = repository.save(mapper.toEntity(movimiento));
		return mapper.toDomain(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Movimiento> findById(UUID id) {
		return repository.findById(id).map(mapper::toDomain);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Movimiento> findByCommandId(UUID commandId) {
		return repository.findByCommandId(commandId).map(mapper::toDomain);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Movimiento> findAll() {
		return repository.findAll().stream().map(mapper::toDomain).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Movimiento> findByCuentaIdAndFechaBetween(UUID cuentaId, Instant from, Instant to) {
		return repository.findByCuentaIdAndFechaBetweenOrderByFechaAsc(cuentaId, from, to).stream()
				.map(mapper::toDomain)
				.toList();
	}

	@Override
	@Transactional
	public void deleteById(UUID id) {
		repository.deleteById(id);
	}
}
