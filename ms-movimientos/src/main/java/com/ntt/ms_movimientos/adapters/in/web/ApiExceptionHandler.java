package com.ntt.ms_movimientos.adapters.in.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.ntt.ms_movimientos.adapters.in.web.dto.ApiResponse;
import com.ntt.ms_movimientos.domain.exception.ConflictException;
import com.ntt.ms_movimientos.domain.exception.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ApiExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

	@Value("${app.api.version:1.0}")
	private String apiVersion;

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(ApiResponse.error("NOT_FOUND", HttpStatus.NOT_FOUND.value(), ex.getMessage(), correlationId(request), apiVersion));
	}

	@ExceptionHandler(ConflictException.class)
	public ResponseEntity<ApiResponse<Void>> handleConflict(ConflictException ex, HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(ApiResponse.error("CONFLICT", HttpStatus.CONFLICT.value(), ex.getMessage(), correlationId(request), apiVersion));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
		String msg = ex.getBindingResult().getAllErrors().stream()
				.findFirst()
				.map(err -> err.getDefaultMessage())
				.orElse("Solicitud inválida");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.error("BAD_REQUEST", HttpStatus.BAD_REQUEST.value(), msg, correlationId(request), apiVersion));
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(ApiResponse.error("NOT_FOUND", HttpStatus.NOT_FOUND.value(), "Recurso no encontrado",
						correlationId(request), apiVersion));
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ApiResponse<Void>> handleMissingParam(MissingServletRequestParameterException ex, HttpServletRequest request) {
		String msg = "Falta el parámetro requerido: " + ex.getParameterName();
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.error("BAD_REQUEST", HttpStatus.BAD_REQUEST.value(), msg, correlationId(request), apiVersion));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
		String msg = "Parámetro inválido: " + ex.getName();
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.error("BAD_REQUEST", HttpStatus.BAD_REQUEST.value(), msg, correlationId(request), apiVersion));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiResponse<Void>> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.error("BAD_REQUEST", HttpStatus.BAD_REQUEST.value(), "Cuerpo de solicitud inválido",
						correlationId(request), apiVersion));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleAny(Exception ex, HttpServletRequest request) {
		log.error("Unhandled exception. correlationId={}", correlationId(request), ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ApiResponse.error("INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error interno del servidor",
						correlationId(request), apiVersion));
	}

	private String correlationId(HttpServletRequest request) {
		String cid = request.getHeader("X-Correlation-Id");
		return cid != null && !cid.isBlank() ? cid : request.getHeader("X-Correlation-ID");
	}
}
