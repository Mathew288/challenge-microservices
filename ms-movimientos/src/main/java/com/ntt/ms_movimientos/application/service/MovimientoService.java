package com.ntt.ms_movimientos.application.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ntt.ms_movimientos.adapters.in.web.dto.CreateMovimientoRequest;
import com.ntt.ms_movimientos.application.dto.MovimientoCommand;
import com.ntt.ms_movimientos.application.service.OutboxService;
import com.ntt.ms_movimientos.adapters.in.web.dto.MovimientoResponse;
import com.ntt.ms_movimientos.adapters.in.web.dto.UpdateMovimientoRequest;
import com.ntt.ms_movimientos.adapters.in.web.mapper.MovimientoWebMapper;
import com.ntt.ms_movimientos.application.port.in.MovimientoUseCase;
import com.ntt.ms_movimientos.application.port.out.MovimientoRepositoryPort;
import com.ntt.ms_movimientos.domain.exception.ResourceNotFoundException;
import com.ntt.ms_movimientos.domain.model.EstadoMovimiento;
import com.ntt.ms_movimientos.domain.model.Movimiento;

@Service
public class MovimientoService implements MovimientoUseCase {

	private final MovimientoRepositoryPort repository;
	private final MovimientoWebMapper mapper;
	private final OutboxService outboxService;

	public MovimientoService(
			MovimientoRepositoryPort repository,
			MovimientoWebMapper mapper,
			OutboxService outboxService
	) {
		this.repository = repository;
		this.mapper = mapper;
		this.outboxService = outboxService;
	}

	@Override
	@Transactional
	public MovimientoResponse create(CreateMovimientoRequest request) {
		Instant now = Instant.now();

		// En la saga real: acá se crearía el movimiento PENDING y se enviaría command a ms-cuentas.
		// En este MVP: dejamos estado PENDING, y el saldoDisponible se completará vía evento.
		UUID sagaId = UUID.randomUUID();
		UUID commandId = UUID.randomUUID();

		Movimiento movimiento = new Movimiento(
				null,
				request.cuentaId(),
				now,
				request.tipo(),
				request.valor(),
				null,
				EstadoMovimiento.PENDING,
				sagaId,
				commandId,
				now
		);

		Movimiento saved = repository.save(movimiento);

		// TX local: persistimos el movimiento + outbox (publicación asíncrona).
		MovimientoCommand command = new MovimientoCommand(
				commandId,
				sagaId,
				request.cuentaId(),
				request.tipo(),
				request.valor().abs(), // en el command enviamos valor positivo
				Instant.now()
		);

		String routingKey = switch (request.tipo()) {
			case DEBITO -> "movimientos.debito-solicitado";
			case CREDITO -> "movimientos.credito-solicitado";
		};

		outboxService.enqueue(
				"Movimiento",
				saved.id(),
				"MovimientoCommand",
				routingKey,
				"banking.events",
				command
		);

		return mapper.toResponse(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public MovimientoResponse getById(UUID id) {
		Movimiento m = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado id=" + id));
		return mapper.toResponse(m);
	}

	@Override
	@Transactional(readOnly = true)
	public List<MovimientoResponse> list() {
		return repository.findAll().stream().map(mapper::toResponse).toList();
	}

	@Override
	@Transactional
	public MovimientoResponse update(UUID id, UpdateMovimientoRequest request) {
		Movimiento current = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado id=" + id));

		Movimiento updated = new Movimiento(
				current.id(),
				current.cuentaId(),
				current.fecha(),
				request.tipo() != null ? request.tipo() : current.tipo(),
				request.valor() != null ? request.valor() : current.valor(),
				current.saldoDisponible(),
				current.estado(),
				current.sagaId(),
				current.commandId(),
				current.createdAt()
		);

		return mapper.toResponse(repository.save(updated));
	}

	@Override
	@Transactional
	public void delete(UUID id) {
		if (repository.findById(id).isEmpty()) {
			throw new ResourceNotFoundException("Movimiento no encontrado id=" + id);
		}
		repository.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<MovimientoResponse> listByCuentaAndDateRange(UUID cuentaId, Instant from, Instant to) {
		return repository.findByCuentaIdAndFechaBetween(cuentaId, from, to).stream().map(mapper::toResponse).toList();
	}
}
