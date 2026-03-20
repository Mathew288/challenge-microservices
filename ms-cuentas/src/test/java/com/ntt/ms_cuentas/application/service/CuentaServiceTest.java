package com.ntt.ms_cuentas.application.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

import com.ntt.ms_cuentas.adapters.in.web.dto.CreateCuentaRequest;
import com.ntt.ms_cuentas.adapters.in.web.mapper.CuentaWebMapper;
import com.ntt.ms_cuentas.application.port.out.CuentaRepositoryPort;
import com.ntt.ms_cuentas.domain.exception.ConflictException;
import com.ntt.ms_cuentas.domain.exception.ResourceNotFoundException;
import com.ntt.ms_cuentas.domain.model.Cuenta;
import com.ntt.ms_cuentas.domain.model.TipoCuenta;

class CuentaServiceTest {

	@Test
	void create_siNumeroCuentaExiste_debeLanzarConflict() {
		CuentaRepositoryPort repo = Mockito.mock(CuentaRepositoryPort.class);
		CuentaWebMapper mapper = new CuentaWebMapper();
		CuentaService service = new CuentaService(repo, mapper);

		when(repo.existsByNumeroCuenta("478758")).thenReturn(true);

		CreateCuentaRequest req = new CreateCuentaRequest(
				"478758",
				TipoCuenta.AHORRO,
				new BigDecimal("2000.00"),
				true,
				UUID.randomUUID()
		);

		assertThrows(ConflictException.class, () -> service.create(req));
	}

	@Test
	void getById_siNoExiste_debeLanzarNotFound() {
		CuentaRepositoryPort repo = Mockito.mock(CuentaRepositoryPort.class);
		CuentaWebMapper mapper = new CuentaWebMapper();
		CuentaService service = new CuentaService(repo, mapper);

		UUID id = UUID.randomUUID();
		when(repo.findById(id)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> service.getById(id));
	}

	@Test
	void create_debeInicializarSaldoActualIgualASaldoInicial() {
		CuentaRepositoryPort repo = Mockito.mock(CuentaRepositoryPort.class);
		CuentaWebMapper mapper = new CuentaWebMapper();
		CuentaService service = new CuentaService(repo, mapper);

		when(repo.existsByNumeroCuenta("225487")).thenReturn(false);

		when(repo.save(any())).thenAnswer(inv -> {
			Cuenta c = inv.getArgument(0, Cuenta.class);
			// simular persistencia (id asignado)
			return new Cuenta(
					UUID.randomUUID(),
					c.numeroCuenta(),
					c.tipo(),
					c.saldoInicial(),
					c.saldoActual(),
					c.estado(),
					c.clienteId(),
					c.createdAt() != null ? c.createdAt() : Instant.now(),
					c.updatedAt() != null ? c.updatedAt() : Instant.now()
			);
		});

		CreateCuentaRequest req = new CreateCuentaRequest(
				"225487",
				TipoCuenta.CORRIENTE,
				new BigDecimal("100.00"),
				true,
				UUID.randomUUID()
		);

		var resp = service.create(req);

		assertEquals(new BigDecimal("100.00"), resp.saldoInicial());
		assertEquals(new BigDecimal("100.00"), resp.saldoActual());
	}
}
