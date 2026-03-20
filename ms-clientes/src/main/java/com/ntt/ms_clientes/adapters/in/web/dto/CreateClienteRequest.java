package com.ntt.ms_clientes.adapters.in.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateClienteRequest(
		@NotBlank @Size(max = 20) String identificacion,
		@NotBlank @Size(max = 120) String nombre,
		@NotBlank @Size(max = 20) String genero,
		@NotNull @Min(0) @Max(150) Integer edad,
		@NotBlank @Size(max = 255) String direccion,
		@NotBlank @Size(max = 30) String telefono,
		@NotBlank @Size(min = 4, max = 100) String password,
		@NotNull Boolean estado
) {}
