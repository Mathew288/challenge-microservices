package com.ntt.ms_movimientos.domain.exception;

public class ConflictException extends RuntimeException {

	public ConflictException(String message) {
		super(message);
	}
}
