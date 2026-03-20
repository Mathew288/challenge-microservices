package com.ntt.ms_clientes.adapters.in.web.controller;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntt.ms_clientes.adapters.in.web.dto.ClienteResponse;
import com.ntt.ms_clientes.adapters.in.web.dto.CreateClienteRequest;
import com.ntt.ms_clientes.application.port.in.ClienteUseCase;

class ClienteControllerWebMvcTest {

	private MockMvc mockMvc;

	private ObjectMapper objectMapper;

	private ClienteUseCase clienteUseCase;

	@BeforeEach
	void setUp() {
		this.objectMapper = new ObjectMapper();
		this.clienteUseCase = mock(ClienteUseCase.class);
		this.mockMvc = standaloneSetup(new ClienteController(clienteUseCase)).build();
	}

	@Test
	void create_returns201_andLocationHeader() throws Exception {
		UUID id = UUID.randomUUID();
		CreateClienteRequest req = new CreateClienteRequest(
				"1101122334",
				"Jose Lema",
				"M",
				30,
				"Otavalo sn y principal",
				"098254785",
				"1234",
				true
		);

		when(clienteUseCase.create(any(CreateClienteRequest.class))).thenReturn(
				new ClienteResponse(
						id,
						req.identificacion(),
						req.nombre(),
						req.genero(),
						req.edad(),
						req.direccion(),
						req.telefono(),
						req.estado(),
						null,
						null
				)
		);

		mockMvc.perform(post("/clientes")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(req)))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/clientes/" + id)));
	}

	@Test
	void delete_returns204() throws Exception {
		UUID id = UUID.randomUUID();
		mockMvc.perform(delete("/clientes/{id}", id))
				.andExpect(status().isNoContent());
	}

	@Test
	void getById_returns200() throws Exception {
		UUID id = UUID.randomUUID();
		when(clienteUseCase.getById(eq(id))).thenReturn(
				new ClienteResponse(
						id,
						"1101122334",
						"Jose Lema",
						"M",
						30,
						"Otavalo sn y principal",
						"098254785",
						true,
						null,
						null
				)
		);

		mockMvc.perform(get("/clientes/{id}", id))
				.andExpect(status().isOk());
	}
}
