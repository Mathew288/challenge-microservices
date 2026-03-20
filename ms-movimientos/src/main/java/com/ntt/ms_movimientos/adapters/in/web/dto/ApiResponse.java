package com.ntt.ms_movimientos.adapters.in.web.dto;

public class ApiResponse<T> {

	private boolean success;
	private String code;
	private int httpStatus;
	private String message;
	private String correlationId;
	private String apiVersion;
	private T data;
	private Object meta;

	public ApiResponse() {
	}

	public ApiResponse(
			boolean success,
			String code,
			int httpStatus,
			String message,
			String correlationId,
			String apiVersion,
			T data,
			Object meta
	) {
		this.success = success;
		this.code = code;
		this.httpStatus = httpStatus;
		this.message = message;
		this.correlationId = correlationId;
		this.apiVersion = apiVersion;
		this.data = data;
		this.meta = meta;
	}

	public static <T> ApiResponse<T> ok(int httpStatus, String message, String correlationId, String apiVersion, T data) {
		return new ApiResponse<>(true, "OK", httpStatus, message, correlationId, apiVersion, data, null);
	}

	public static <T> ApiResponse<T> error(
			String code,
			int httpStatus,
			String message,
			String correlationId,
			String apiVersion
	) {
		return new ApiResponse<>(false, code, httpStatus, message, correlationId, apiVersion, null, null);
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(int httpStatus) {
		this.httpStatus = httpStatus;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	public String getApiVersion() {
		return apiVersion;
	}

	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public Object getMeta() {
		return meta;
	}

	public void setMeta(Object meta) {
		this.meta = meta;
	}
}
