package com.ntt.ms_movimientos.adapters.in.web.controller;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ntt.ms_movimientos.adapters.in.web.dto.ApiResponse;
import com.ntt.ms_movimientos.adapters.in.web.dto.ReporteEstadoCuentaResponse;
import com.ntt.ms_movimientos.application.service.ReporteEstadoCuentaService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/reportes")
public class ReporteController {

	private final ReporteEstadoCuentaService reporteEstadoCuentaService;

	@Value("${app.api.version:1.0}")
	private String apiVersion;

	public ReporteController(ReporteEstadoCuentaService reporteEstadoCuentaService) {
		this.reporteEstadoCuentaService = reporteEstadoCuentaService;
	}

	@GetMapping("/estado-cuenta")
	public ResponseEntity<ApiResponse<ReporteEstadoCuentaResponse>> estadoCuenta(
			@RequestParam UUID clienteId,
			@RequestParam Optional<Instant> from,
			@RequestParam Optional<Instant> to,
			HttpServletRequest http
	) {
		ReporteEstadoCuentaResponse data = reporteEstadoCuentaService.generar(
				clienteId,
				from != null ? from.orElse(null) : null,
				to != null ? to.orElse(null) : null
		);
		return ResponseEntity.ok(ApiResponse.ok(HttpStatus.OK.value(), "Operación realizada con éxito.", correlationId(http), apiVersion, data));
	}

	private String correlationId(HttpServletRequest request) {
		String cid = request.getHeader("X-Correlation-Id");
		return cid != null && !cid.isBlank() ? cid : request.getHeader("X-Correlation-ID");
	}
}
