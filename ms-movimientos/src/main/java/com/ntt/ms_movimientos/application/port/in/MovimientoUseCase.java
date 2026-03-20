package com.ntt.ms_movimientos.application.port.in;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.ntt.ms_movimientos.adapters.in.web.dto.CreateMovimientoRequest;
import com.ntt.ms_movimientos.adapters.in.web.dto.MovimientoResponse;
import com.ntt.ms_movimientos.adapters.in.web.dto.UpdateMovimientoRequest;

public interface MovimientoUseCase {

	MovimientoResponse create(CreateMovimientoRequest request);

	MovimientoResponse getById(UUID id);

	List<MovimientoResponse> list();

	MovimientoResponse update(UUID id, UpdateMovimientoRequest request);

	void delete(UUID id);

	List<MovimientoResponse> listByCuentaAndDateRange(UUID cuentaId, Instant from, Instant to);
}
