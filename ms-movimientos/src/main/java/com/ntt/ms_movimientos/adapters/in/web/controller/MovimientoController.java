package com.ntt.ms_movimientos.adapters.in.web.controller;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ntt.ms_movimientos.adapters.in.web.dto.ApiResponse;
import com.ntt.ms_movimientos.adapters.in.web.dto.CreateMovimientoRequest;
import com.ntt.ms_movimientos.adapters.in.web.dto.MovimientoResponse;
import com.ntt.ms_movimientos.adapters.in.web.dto.UpdateMovimientoRequest;
import com.ntt.ms_movimientos.application.port.in.MovimientoUseCase;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/movimientos")
public class MovimientoController {

	private final MovimientoUseCase useCase;

	@Value("${app.api.version:1.0}")
	private String apiVersion;

	public MovimientoController(MovimientoUseCase useCase) {
		this.useCase = useCase;
	}

	@PostMapping
	public ResponseEntity<ApiResponse<MovimientoResponse>> create(@Valid @RequestBody CreateMovimientoRequest request,
			HttpServletRequest http) {

		MovimientoResponse created = useCase.create(request);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.ok(HttpStatus.CREATED.value(), "Operación realizada con éxito.", correlationId(http), apiVersion, created));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<MovimientoResponse>> getById(@PathVariable UUID id, HttpServletRequest http) {
		MovimientoResponse data = useCase.getById(id);
		return ResponseEntity.ok(ApiResponse.ok(HttpStatus.OK.value(), "Operación realizada con éxito.", correlationId(http), apiVersion, data));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<MovimientoResponse>>> list(HttpServletRequest http) {
		List<MovimientoResponse> data = useCase.list();
		return ResponseEntity.ok(ApiResponse.ok(HttpStatus.OK.value(), "Operación realizada con éxito.", correlationId(http), apiVersion, data));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<ApiResponse<MovimientoResponse>> update(@PathVariable UUID id, @RequestBody UpdateMovimientoRequest request,
			HttpServletRequest http) {
		MovimientoResponse data = useCase.update(id, request);
		return ResponseEntity.ok(ApiResponse.ok(HttpStatus.OK.value(), "Operación realizada con éxito.", correlationId(http), apiVersion, data));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id, HttpServletRequest http) {
		useCase.delete(id);
		return ResponseEntity.ok(ApiResponse.ok(HttpStatus.OK.value(), "Operación realizada con éxito.", correlationId(http), apiVersion, null));
	}

	@GetMapping("/by-cuenta")
	public ResponseEntity<ApiResponse<List<MovimientoResponse>>> listByCuentaAndDateRange(
			@RequestParam UUID cuentaId,
			@RequestParam Instant from,
			@RequestParam Instant to,
			HttpServletRequest http
	) {
		List<MovimientoResponse> data = useCase.listByCuentaAndDateRange(cuentaId, from, to);
		return ResponseEntity.ok(ApiResponse.ok(HttpStatus.OK.value(), "Operación realizada con éxito.", correlationId(http), apiVersion, data));
	}

	private String correlationId(HttpServletRequest request) {
		String cid = request.getHeader("X-Correlation-Id");
		return cid != null && !cid.isBlank() ? cid : request.getHeader("X-Correlation-ID");
	}
}
