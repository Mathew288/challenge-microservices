package com.ntt.ms_clientes.application.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ntt.ms_clientes.adapters.in.web.dto.ClienteResponse;
import com.ntt.ms_clientes.adapters.in.web.dto.CreateClienteRequest;
import com.ntt.ms_clientes.adapters.in.web.dto.UpdateClienteRequest;
import com.ntt.ms_clientes.adapters.out.persistence.entity.ClienteEntity;
import com.ntt.ms_clientes.adapters.out.persistence.repository.ClienteJpaRepository;
import com.ntt.ms_clientes.application.port.in.ClienteUseCase;
import com.ntt.ms_clientes.domain.exception.ConflictException;
import com.ntt.ms_clientes.domain.exception.ResourceNotFoundException;

@Service
public class ClienteService implements ClienteUseCase {

	private final ClienteJpaRepository repository;
	private final PasswordEncoder passwordEncoder;

	public ClienteService(ClienteJpaRepository repository, PasswordEncoder passwordEncoder) {
		this.repository = repository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	@Transactional
	public ClienteResponse create(CreateClienteRequest request) {
		// Si existe un registro con la misma identificación pero está inactivo (soft delete),
		// lo reactivamos en lugar de devolver CONFLICT.
		ClienteEntity existing = repository.findByIdentificacion(request.identificacion()).orElse(null);
		if (existing != null) {
			if (Boolean.TRUE.equals(existing.getEstado())) {
				throw new ConflictException("Ya existe un cliente con identificacion=" + request.identificacion());
			}

			existing.setNombre(request.nombre());
			existing.setGenero(request.genero());
			existing.setEdad(request.edad());
			existing.setDireccion(request.direccion());
			existing.setTelefono(request.telefono());
			existing.setPasswordHash(passwordEncoder.encode(request.password()));
			existing.setEstado(request.estado());
			existing.setUpdatedAt(Instant.now());

			ClienteEntity saved = repository.save(existing);
			return toResponse(saved);
		}

		Instant now = Instant.now();
		String passwordHash = passwordEncoder.encode(request.password());

		ClienteEntity entity = new ClienteEntity(
				null,
				request.identificacion(),
				request.nombre(),
				request.genero(),
				request.edad(),
				request.direccion(),
				request.telefono(),
				passwordHash,
				request.estado(),
				now,
				now
		);

		ClienteEntity saved = repository.save(entity);
		return toResponse(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public ClienteResponse getById(UUID id) {
		// Regla: cuando se consulta por un identificador concreto y no existe, se responde NOT_FOUND
		ClienteEntity entity = repository.findById(id)
				.filter(e -> Boolean.TRUE.equals(e.getEstado()))
				.orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado id=" + id));
		return toResponse(entity);
	}

	@Override
	@Transactional(readOnly = true)
	public ClienteResponse getByIdentificacion(String identificacion) {
		ClienteEntity entity = repository.findByIdentificacion(identificacion)
				.filter(e -> Boolean.TRUE.equals(e.getEstado()))
				.orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado identificacion=" + identificacion));
		return toResponse(entity);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ClienteResponse> list() {
		return repository.findAll().stream()
				.filter(e -> Boolean.TRUE.equals(e.getEstado()))
				.map(this::toResponse)
				.toList();
	}

	@Override
	@Transactional
	public ClienteResponse update(UUID id, UpdateClienteRequest request) {
		ClienteEntity entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado id=" + id));

		if (request.nombre() != null && !request.nombre().isBlank()) {
			entity.setNombre(request.nombre());
		}
		if (request.genero() != null && !request.genero().isBlank()) {
			entity.setGenero(request.genero());
		}
		if (request.edad() != null) {
			entity.setEdad(request.edad());
		}
		if (request.direccion() != null && !request.direccion().isBlank()) {
			entity.setDireccion(request.direccion());
		}
		if (request.telefono() != null && !request.telefono().isBlank()) {
			entity.setTelefono(request.telefono());
		}
		if (request.password() != null && !request.password().isBlank()) {
			entity.setPasswordHash(passwordEncoder.encode(request.password()));
		}
		if (request.estado() != null) {
			entity.setEstado(request.estado());
		}
		entity.setUpdatedAt(Instant.now());

		ClienteEntity saved = repository.save(entity);
		return toResponse(saved);
	}

	@Override
	@Transactional
	public void delete(UUID id) {
		ClienteEntity entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado id=" + id));

		if (Boolean.FALSE.equals(entity.getEstado())) {
			// idempotente: ya está inactivo
			return;
		}

		entity.setEstado(false);
		entity.setUpdatedAt(Instant.now());
		repository.save(entity);
	}

	private ClienteResponse toResponse(ClienteEntity e) {
		return new ClienteResponse(
				e.getId(),
				e.getIdentificacion(),
				e.getNombre(),
				e.getGenero(),
				e.getEdad(),
				e.getDireccion(),
				e.getTelefono(),
				e.getEstado(),
				e.getCreatedAt(),
				e.getUpdatedAt()
		);
	}
}
