package com.ntt.ms_clientes.adapters.in.web.dto;

import java.util.Map;

public record ApiResponse<T>(
		boolean success,
		String code,
		int httpStatus,
		String message,
		String correlationId,
		String apiVersion,
		T data,
		Map<String, Object> meta
) {

	public static final String DEFAULT_API_VERSION = "1.0";

	public static <T> ApiResponse<T> ok(int httpStatus, String message, String correlationId, T data) {
		return new ApiResponse<>(
				true,
				"OK",
				httpStatus,
				message,
				correlationId,
				DEFAULT_API_VERSION,
				data,
				null
		);
	}

	public static <T> ApiResponse<T> error(int httpStatus, String code, String message, String correlationId, Map<String, Object> meta) {
		return new ApiResponse<>(
				false,
				code,
				httpStatus,
				message,
				correlationId,
				DEFAULT_API_VERSION,
				null,
				meta
		);
	}
}
