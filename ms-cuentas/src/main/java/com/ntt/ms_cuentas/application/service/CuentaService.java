package com.ntt.ms_cuentas.application.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ntt.ms_cuentas.adapters.in.web.dto.CreateCuentaRequest;
import com.ntt.ms_cuentas.adapters.in.web.dto.CuentaResponse;
import com.ntt.ms_cuentas.adapters.in.web.dto.UpdateCuentaRequest;
import com.ntt.ms_cuentas.adapters.in.web.mapper.CuentaWebMapper;
import com.ntt.ms_cuentas.application.port.in.CuentaUseCase;
import com.ntt.ms_cuentas.application.port.out.CuentaRepositoryPort;
import com.ntt.ms_cuentas.domain.exception.ConflictException;
import com.ntt.ms_cuentas.domain.exception.ResourceNotFoundException;
import com.ntt.ms_cuentas.domain.model.Cuenta;

@Service
public class CuentaService implements CuentaUseCase {

	private final CuentaRepositoryPort repository;
	private final CuentaWebMapper mapper;

	public CuentaService(CuentaRepositoryPort repository, CuentaWebMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	@Override
	@Transactional
	public CuentaResponse create(CreateCuentaRequest request) {
		if (repository.existsByNumeroCuenta(request.numeroCuenta())) {
			throw new ConflictException("Ya existe una cuenta con numeroCuenta=" + request.numeroCuenta());
		}

		Instant now = Instant.now();
		BigDecimal saldoInicial = request.saldoInicial();
		Cuenta cuenta = new Cuenta(
				null,
				request.numeroCuenta(),
				request.tipo(),
				saldoInicial,
				saldoInicial, // saldoActual arranca = saldoInicial
				request.estado(),
				request.clienteId(),
				now,
				now
		);

		return mapper.toResponse(repository.save(cuenta));
	}

	@Override
	@Transactional(readOnly = true)
	public CuentaResponse getById(UUID id) {
		Cuenta cuenta = repository.findById(id)
				.filter(c -> c.estado())
				.orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada id=" + id));
		return mapper.toResponse(cuenta);
	}

	@Override
	@Transactional(readOnly = true)
	public CuentaResponse getByNumeroCuenta(String numeroCuenta) {
		Cuenta cuenta = repository.findByNumeroCuenta(numeroCuenta)
				.filter(c -> c.estado())
				.orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada numeroCuenta=" + numeroCuenta));
		return mapper.toResponse(cuenta);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CuentaResponse> list() {
		return repository.findAll().stream()
				.filter(Cuenta::estado)
				.map(mapper::toResponse)
				.toList();
	}

	@Override
	@Transactional
	public CuentaResponse update(UUID id, UpdateCuentaRequest request) {
		Cuenta current = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada id=" + id));

		String newNumeroCuenta = request.numeroCuenta() != null ? request.numeroCuenta() : current.numeroCuenta();
		if (!newNumeroCuenta.equals(current.numeroCuenta()) && repository.existsByNumeroCuenta(newNumeroCuenta)) {
			throw new ConflictException("Ya existe una cuenta con numeroCuenta=" + newNumeroCuenta);
		}

		Cuenta updated = new Cuenta(
				current.id(),
				newNumeroCuenta,
				request.tipo() != null ? request.tipo() : current.tipo(),
				request.saldoInicial() != null ? request.saldoInicial() : current.saldoInicial(),
				current.saldoActual(), // el saldo actual lo modifica ms-movimientos/saga; acá no lo tocamos
				request.estado(),
				current.clienteId(),
				current.createdAt(),
				Instant.now()
		);

		return mapper.toResponse(repository.save(updated));
	}

	@Override
	@Transactional
	public void delete(UUID id) {
		// Soft delete: no se elimina físicamente, solo se marca como inactiva (estado=false)
		Cuenta current = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada id=" + id));

		if (!current.estado()) {
			// idempotente: ya está inactiva
			return;
		}

		Cuenta updated = new Cuenta(
				current.id(),
				current.numeroCuenta(),
				current.tipo(),
				current.saldoInicial(),
				current.saldoActual(),
				false,
				current.clienteId(),
				current.createdAt(),
				Instant.now()
		);

		repository.save(updated);
	}
}
