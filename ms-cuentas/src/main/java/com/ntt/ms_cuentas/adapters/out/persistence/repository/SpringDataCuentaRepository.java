package com.ntt.ms_cuentas.adapters.out.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ntt.ms_cuentas.adapters.out.persistence.entity.CuentaEntity;

public interface SpringDataCuentaRepository extends JpaRepository<CuentaEntity, UUID> {

	Optional<CuentaEntity> findByNumeroCuenta(String numeroCuenta);

	boolean existsByNumeroCuenta(String numeroCuenta);
}
