package com.ntt.ms_clientes.adapters.out.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ntt.ms_clientes.adapters.out.persistence.entity.ClienteEntity;

public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, UUID> {

	Optional<ClienteEntity> findByIdentificacion(String identificacion);

	boolean existsByIdentificacion(String identificacion);

	// Consultas explícitas para registros inactivos (estado=false) cuando se necesiten
	Optional<ClienteEntity> findByIdAndEstadoFalse(UUID id);

	Optional<ClienteEntity> findByIdentificacionAndEstadoFalse(String identificacion);

	List<ClienteEntity> findAllByEstadoFalse();
}
