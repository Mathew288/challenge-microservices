package com.ntt.ms_clientes.adapters.in.web.dto;

import java.time.Instant;
import java.util.UUID;

public record ClienteResponse(
	UUID id,
	String identificacion,
	String nombre,
	String genero,
	Integer edad,
	String direccion,
	String telefono,
	Boolean estado,
	Instant createdAt,
	Instant updatedAt
) {}
