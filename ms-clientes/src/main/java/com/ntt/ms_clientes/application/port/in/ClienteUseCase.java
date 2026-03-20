package com.ntt.ms_clientes.application.port.in;

import java.util.List;
import java.util.UUID;

import com.ntt.ms_clientes.adapters.in.web.dto.ClienteResponse;
import com.ntt.ms_clientes.adapters.in.web.dto.CreateClienteRequest;
import com.ntt.ms_clientes.adapters.in.web.dto.UpdateClienteRequest;

public interface ClienteUseCase {

	ClienteResponse create(CreateClienteRequest request);

	ClienteResponse getById(UUID id);

	ClienteResponse getByIdentificacion(String identificacion);

	List<ClienteResponse> list();

	ClienteResponse update(UUID id, UpdateClienteRequest request);

	void delete(UUID id);
}
