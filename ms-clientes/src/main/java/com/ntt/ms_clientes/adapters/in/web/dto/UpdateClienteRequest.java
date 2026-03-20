package com.ntt.ms_clientes.adapters.in.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateClienteRequest(
		@Size(max = 120) String nombre,
		@Size(max = 20) String genero,
		@Min(0) @Max(150) Integer edad,
		@Size(max = 255) String direccion,
		@Size(max = 30) String telefono,
		@Size(min = 4, max = 100) String password,
		Boolean estado
) {}
