package com.ntt.ms_clientes.adapters.in.web.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ntt.ms_clientes.adapters.in.web.dto.ApiResponse;
import com.ntt.ms_clientes.adapters.in.web.dto.ClienteResponse;
import com.ntt.ms_clientes.adapters.in.web.dto.CreateClienteRequest;
import com.ntt.ms_clientes.adapters.in.web.dto.UpdateClienteRequest;
import com.ntt.ms_clientes.application.port.in.ClienteUseCase;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

	private final ClienteUseCase clienteUseCase;

	public ClienteController(ClienteUseCase clienteUseCase) {
		this.clienteUseCase = clienteUseCase;
	}

	@PostMapping
	public ResponseEntity<ApiResponse<ClienteResponse>> create(@Valid @RequestBody CreateClienteRequest request) {
		ClienteResponse created = clienteUseCase.create(request);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(created.id())
				.toUri();

		return ResponseEntity.created(location)
				.body(ApiResponse.ok(HttpStatus.CREATED.value(), "Operación realizada con éxito.", correlationId(), created));
	}

	@GetMapping("/{id}")
	public ApiResponse<ClienteResponse> getById(@PathVariable UUID id) {
		ClienteResponse data = clienteUseCase.getById(id);
		String message = data != null ? "Operación realizada con éxito." : "Sin resultados.";
		return ApiResponse.ok(HttpStatus.OK.value(), message, correlationId(), data);
	}

	@GetMapping("/identificacion/{identificacion}")
	public ApiResponse<ClienteResponse> getByIdentificacion(@PathVariable String identificacion) {
		ClienteResponse data = clienteUseCase.getByIdentificacion(identificacion);
		String message = data != null ? "Operación realizada con éxito." : "Sin resultados.";
		return ApiResponse.ok(HttpStatus.OK.value(), message, correlationId(), data);
	}

	@GetMapping
	public ApiResponse<List<ClienteResponse>> list() {
		List<ClienteResponse> data = clienteUseCase.list();
		return ApiResponse.ok(HttpStatus.OK.value(), "Operación realizada con éxito.", correlationId(), data);
	}

	@PatchMapping("/{id}")
	public ApiResponse<ClienteResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateClienteRequest request) {
		ClienteResponse data = clienteUseCase.update(id, request);
		return ApiResponse.ok(HttpStatus.OK.value(), "Operación realizada con éxito.", correlationId(), data);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
		clienteUseCase.delete(id);
		return ResponseEntity.ok(ApiResponse.ok(HttpStatus.OK.value(), "Operación realizada con éxito.", correlationId(), null));
	}

	private String correlationId() {
		// lo setea CorrelationIdFilter
		Object value = org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes()
				.getAttribute(com.ntt.ms_clientes.config.CorrelationIdFilter.REQUEST_ATTR,
						org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST);
		return value != null ? value.toString() : null;
	}
}
