package com.ntt.ms_cuentas.application.port.in;

import java.util.List;
import java.util.UUID;

import com.ntt.ms_cuentas.adapters.in.web.dto.CuentaResponse;
import com.ntt.ms_cuentas.adapters.in.web.dto.CreateCuentaRequest;
import com.ntt.ms_cuentas.adapters.in.web.dto.UpdateCuentaRequest;

public interface CuentaUseCase {

	CuentaResponse create(CreateCuentaRequest request);

	CuentaResponse getById(UUID id);

	CuentaResponse getByNumeroCuenta(String numeroCuenta);

	List<CuentaResponse> list();

	CuentaResponse update(UUID id, UpdateCuentaRequest request);

	void delete(UUID id);
}
