package com.ntt.ms_movimientos.application.service;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ntt.ms_movimientos.application.dto.SaldoActualizadoEvent;
import com.ntt.ms_movimientos.application.dto.SaldoRechazadoEvent;
import com.ntt.ms_movimientos.application.port.in.CuentaSaldoEventHandler;
import com.ntt.ms_movimientos.application.port.out.MovimientoRepositoryPort;
import com.ntt.ms_movimientos.domain.model.EstadoMovimiento;
import com.ntt.ms_movimientos.domain.model.Movimiento;

@Service
public class CuentaSaldoEventService implements CuentaSaldoEventHandler {

	private static final String CONSUMER_NAME = "ms-movimientos.cuenta-saldo-events";

	private final MovimientoRepositoryPort repository;
	private final ProcessedMessageService processedMessageService;

	public CuentaSaldoEventService(MovimientoRepositoryPort repository, ProcessedMessageService processedMessageService) {
		this.repository = repository;
		this.processedMessageService = processedMessageService;
	}

	@Override
	@Transactional
	public void onSaldoActualizado(SaldoActualizadoEvent event) {
		// idempotencia: si ya procesamos este commandId (message id), no hacemos nada.
		if (!processedMessageService.markProcessed(CONSUMER_NAME, event.commandId())) {
			return;
		}

		Movimiento m = repository.findByCommandId(event.commandId())
				.orElse(null);

		if (m == null) {
			// Orden: evento llegó antes que el movimiento exista. Permitimos re-proceso posterior si llega de nuevo.
			return;
		}

		Movimiento updated = new Movimiento(
				m.id(),
				m.cuentaId(),
				m.fecha(),
				m.tipo(),
				m.valor(),
				event.saldoDisponible(),
				EstadoMovimiento.APPLIED,
				m.sagaId(),
				m.commandId(),
				m.createdAt()
		);

		repository.save(updated);
	}

	@Override
	@Transactional
	public void onSaldoRechazado(SaldoRechazadoEvent event) {
		if (!processedMessageService.markProcessed(CONSUMER_NAME, event.commandId())) {
			return;
		}

		Movimiento m = repository.findByCommandId(event.commandId())
				.orElse(null);

		if (m == null) {
			return;
		}

		Movimiento updated = new Movimiento(
				m.id(),
				m.cuentaId(),
				m.fecha(),
				m.tipo(),
				m.valor(),
				m.saldoDisponible(),
				EstadoMovimiento.REJECTED,
				m.sagaId(),
				m.commandId(),
				m.createdAt()
		);

		repository.save(updated);
	}
}
