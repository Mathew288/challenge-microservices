package com.ntt.ms_cuentas.adapters.in.web;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.ntt.ms_cuentas.adapters.in.web.dto.ApiResponse;
import com.ntt.ms_cuentas.config.CorrelationIdFilter;
import com.ntt.ms_cuentas.domain.exception.ConflictException;
import com.ntt.ms_cuentas.domain.exception.ResourceNotFoundException;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ApiResponse<Void> handleNotFound(ResourceNotFoundException ex) {
		return ApiResponse.error(
				HttpStatus.NOT_FOUND.value(),
				"NOT_FOUND",
				ex.getMessage(),
				correlationId(),
				null
		);
	}

	@ExceptionHandler(ConflictException.class)
	public ApiResponse<Void> handleConflict(ConflictException ex) {
		return ApiResponse.error(
				HttpStatus.CONFLICT.value(),
				"CONFLICT",
				ex.getMessage(),
				correlationId(),
				null
		);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ApiResponse<Void> handleValidation(MethodArgumentNotValidException ex) {
		Map<String, String> fieldErrors = ex.getBindingResult()
				.getAllErrors()
				.stream()
				.filter(FieldError.class::isInstance)
				.map(FieldError.class::cast)
				.collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (a, b) -> a,
						LinkedHashMap::new));

		Map<String, Object> meta = Map.of("errors", fieldErrors);

		return ApiResponse.error(
				HttpStatus.BAD_REQUEST.value(),
				"VALIDATION_ERROR",
				"Existen campos inválidos en la solicitud",
				correlationId(),
				meta
		);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ApiResponse<Void> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
		Map<String, Object> meta = Map.of(
				"parameter", ex.getName(),
				"value", ex.getValue(),
				"expectedType", ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : null
		);

		return ApiResponse.error(
				HttpStatus.BAD_REQUEST.value(),
				"BAD_REQUEST",
				"Parámetro inválido",
				correlationId(),
				meta
		);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ApiResponse<Void> handleNotReadable(HttpMessageNotReadableException ex) {
		return ApiResponse.error(
				HttpStatus.BAD_REQUEST.value(),
				"BAD_REQUEST",
				"Body inválido o JSON mal formado",
				correlationId(),
				null
		);
	}

	@ExceptionHandler({NoHandlerFoundException.class, MissingPathVariableException.class})
	public ApiResponse<Void> handleNoHandler(Exception ex) {
		return ApiResponse.error(
				HttpStatus.NOT_FOUND.value(),
				"NOT_FOUND",
				"Recurso no encontrado",
				correlationId(),
				null
		);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ApiResponse<Void> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
		return ApiResponse.error(
				HttpStatus.METHOD_NOT_ALLOWED.value(),
				"METHOD_NOT_ALLOWED",
				"Método HTTP no permitido",
				correlationId(),
				Map.of("method", ex.getMethod())
		);
	}

	@ExceptionHandler(Exception.class)
	public ApiResponse<Void> handleGeneric(Exception ex) {
		return ApiResponse.error(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"INTERNAL_ERROR",
				"Error interno del servidor",
				correlationId(),
				null
		);
	}

	private String correlationId() {
		Object value = RequestContextHolder.currentRequestAttributes()
				.getAttribute(CorrelationIdFilter.REQUEST_ATTR, RequestAttributes.SCOPE_REQUEST);
		return value != null ? value.toString() : null;
	}
}
