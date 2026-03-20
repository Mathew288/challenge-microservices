package com.ntt.ms_movimientos.adapters.in.web.controller;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntt.ms_movimientos.adapters.in.web.dto.CreateMovimientoRequest;
import com.ntt.ms_movimientos.adapters.in.web.dto.MovimientoResponse;
import com.ntt.ms_movimientos.application.port.in.MovimientoUseCase;
import com.ntt.ms_movimientos.domain.model.EstadoMovimiento;
import com.ntt.ms_movimientos.domain.model.TipoMovimiento;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(
		properties = {
				"spring.flyway.enabled=false",
				"spring.datasource.url=jdbc:h2:mem:ms_movimientos_test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH",
				"spring.datasource.driverClassName=org.h2.Driver",
				"spring.datasource.username=sa",
				"spring.datasource.password=",
				"spring.jpa.hibernate.ddl-auto=none",
				"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
				"spring.rabbitmq.listener.simple.auto-startup=false",
				"spring.rabbitmq.listener.direct.auto-startup=false",
				"spring.rabbitmq.dynamic=false",
				"spring.task.scheduling.enabled=false",
				"app.outbox.publisher.enabled=false"
		}
)
class MovimientoControllerWebMvcTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@org.junit.jupiter.api.BeforeEach
	void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	@MockitoBean
	private MovimientoUseCase useCase;

	@Test
	void shouldCreateMovimiento() throws Exception {
		UUID id = UUID.randomUUID();
		UUID cuentaId = UUID.randomUUID();
		UUID sagaId = UUID.randomUUID();
		UUID commandId = UUID.randomUUID();
		Instant now = Instant.parse("2026-01-01T00:00:00Z");

		CreateMovimientoRequest req = new CreateMovimientoRequest(cuentaId, TipoMovimiento.CREDITO, new BigDecimal("10.00"));

		MovimientoResponse resp = new MovimientoResponse(
				id,
				cuentaId,
				now,
				TipoMovimiento.CREDITO,
				new BigDecimal("10.00"),
				new BigDecimal("110.00"),
				EstadoMovimiento.APPLIED,
				sagaId,
				commandId,
				now
		);

		when(useCase.create(any(CreateMovimientoRequest.class))).thenReturn(resp);

		mockMvc.perform(post("/movimientos")
						.contentType(MediaType.APPLICATION_JSON)
						.header("X-Correlation-Id", "test-corr")
						.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.id").value(id.toString()))
				.andExpect(jsonPath("$.correlationId").value("test-corr"));
	}

	@Test
	void shouldGetMovimientoById() throws Exception {
		UUID id = UUID.randomUUID();
		UUID cuentaId = UUID.randomUUID();
		Instant now = Instant.parse("2026-01-01T00:00:00Z");

		MovimientoResponse resp = new MovimientoResponse(
				id,
				cuentaId,
				now,
				TipoMovimiento.DEBITO,
				new BigDecimal("5.00"),
				new BigDecimal("95.00"),
				EstadoMovimiento.APPLIED,
				UUID.randomUUID(),
				UUID.randomUUID(),
				now
		);

		when(useCase.getById(id)).thenReturn(resp);

		mockMvc.perform(get("/movimientos/{id}", id)
						.header("X-Correlation-Id", "test-corr"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.id").value(id.toString()));
	}
}
