package com.ntt.ms_cuentas.application.service;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ntt.ms_cuentas.application.dto.MovimientoCommand;
import com.ntt.ms_cuentas.application.dto.SaldoActualizadoEvent;
import com.ntt.ms_cuentas.application.dto.SaldoRechazadoEvent;
import com.ntt.ms_cuentas.application.port.in.MovimientoCommandHandler;
import com.ntt.ms_cuentas.application.port.out.CuentaRepositoryPort;
import com.ntt.ms_cuentas.application.port.out.CuentaSaldoPublisherPort;
import com.ntt.ms_cuentas.domain.model.Cuenta;
import com.ntt.ms_cuentas.domain.model.TipoMovimiento;

@Service
public class MovimientoCommandService implements MovimientoCommandHandler {

	private final CuentaRepositoryPort cuentaRepository;
	private final CuentaSaldoPublisherPort publisher;

	public MovimientoCommandService(CuentaRepositoryPort cuentaRepository, CuentaSaldoPublisherPort publisher) {
		this.cuentaRepository = cuentaRepository;
		this.publisher = publisher;
	}

	@Override
	@Transactional
	public void handle(MovimientoCommand command) {
		Cuenta cuenta = cuentaRepository.findById(command.cuentaId())
				.orElse(null);

		if (cuenta == null || !cuenta.estado()) {
			publisher.publishSaldoRechazado(new SaldoRechazadoEvent(
					command.commandId(),
					command.sagaId(),
					command.cuentaId(),
					"Cuenta no encontrada o inactiva",
					Instant.now()
			));
			return;
		}

		BigDecimal saldoActual = cuenta.saldoActual();
		BigDecimal nuevoSaldo;

		if (command.tipoMovimiento() == TipoMovimiento.DEBITO) {
			// Débito: valor debe ser positivo en el command; se resta
			if (saldoActual.compareTo(command.valor()) < 0) {
				publisher.publishSaldoRechazado(new SaldoRechazadoEvent(
						command.commandId(),
						command.sagaId(),
						command.cuentaId(),
						"Saldo no disponible",
						Instant.now()
				));
				return;
			}
			nuevoSaldo = saldoActual.subtract(command.valor());
		} else {
			// Crédito: se suma
			nuevoSaldo = saldoActual.add(command.valor());
		}

		Cuenta updated = new Cuenta(
				cuenta.id(),
				cuenta.numeroCuenta(),
				cuenta.tipo(),
				cuenta.saldoInicial(),
				nuevoSaldo,
				cuenta.estado(),
				cuenta.clienteId(),
				cuenta.createdAt(),
				Instant.now()
		);

		cuentaRepository.save(updated);

		publisher.publishSaldoActualizado(new SaldoActualizadoEvent(
				command.commandId(),
				command.sagaId(),
				command.cuentaId(),
				nuevoSaldo,
				Instant.now()
		));
	}
}
