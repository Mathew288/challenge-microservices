package com.ntt.ms_cuentas.adapters.in.web.controller;

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

import com.ntt.ms_cuentas.adapters.in.web.dto.ApiResponse;
import com.ntt.ms_cuentas.adapters.in.web.dto.CreateCuentaRequest;
import com.ntt.ms_cuentas.adapters.in.web.dto.CuentaResponse;
import com.ntt.ms_cuentas.adapters.in.web.dto.UpdateCuentaRequest;
import com.ntt.ms_cuentas.application.port.in.CuentaUseCase;
import com.ntt.ms_cuentas.config.CorrelationIdFilter;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/cuentas")
public class CuentaController {

	private final CuentaUseCase cuentaUseCase;

	public CuentaController(CuentaUseCase cuentaUseCase) {
		this.cuentaUseCase = cuentaUseCase;
	}

	@PostMapping
	public ResponseEntity<ApiResponse<CuentaResponse>> create(@Valid @RequestBody CreateCuentaRequest request) {
		CuentaResponse created = cuentaUseCase.create(request);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(created.id())
				.toUri();

		return ResponseEntity.created(location)
				.body(ApiResponse.ok(HttpStatus.CREATED.value(), "Operación realizada con éxito.", correlationId(), created));
	}

	@GetMapping("/{id}")
	public ApiResponse<CuentaResponse> getById(@PathVariable UUID id) {
		CuentaResponse data = cuentaUseCase.getById(id);
		String message = data != null ? "Operación realizada con éxito." : "Sin resultados.";
		return ApiResponse.ok(HttpStatus.OK.value(), message, correlationId(), data);
	}

	@GetMapping("/numero/{numeroCuenta}")
	public ApiResponse<CuentaResponse> getByNumeroCuenta(@PathVariable String numeroCuenta) {
		CuentaResponse data = cuentaUseCase.getByNumeroCuenta(numeroCuenta);
		String message = data != null ? "Operación realizada con éxito." : "Sin resultados.";
		return ApiResponse.ok(HttpStatus.OK.value(), message, correlationId(), data);
	}

	@GetMapping
	public ApiResponse<List<CuentaResponse>> list() {
		List<CuentaResponse> data = cuentaUseCase.list();
		return ApiResponse.ok(HttpStatus.OK.value(), "Operación realizada con éxito.", correlationId(), data);
	}

	@PatchMapping("/{id}")
	public ApiResponse<CuentaResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateCuentaRequest request) {
		CuentaResponse data = cuentaUseCase.update(id, request);
		return ApiResponse.ok(HttpStatus.OK.value(), "Operación realizada con éxito.", correlationId(), data);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
		cuentaUseCase.delete(id);
		return ResponseEntity.ok(ApiResponse.ok(HttpStatus.OK.value(), "Operación realizada con éxito.", correlationId(), null));
	}

	private String correlationId() {
		Object value = org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes()
				.getAttribute(CorrelationIdFilter.REQUEST_ATTR,
						org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST);
		return value != null ? value.toString() : null;
	}
}
