package com.ntt.ms_cuentas.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.ntt.ms_cuentas.domain.model.Cuenta;

public interface CuentaRepositoryPort {

	Cuenta save(Cuenta cuenta);

	Optional<Cuenta> findById(UUID id);

	Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);

	List<Cuenta> findAll();

	void deleteById(UUID id);

	boolean existsByNumeroCuenta(String numeroCuenta);
}
