package com.ntt.ms_cuentas.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ntt.ms_cuentas.adapters.out.persistence.entity.CuentaEntity;
import com.ntt.ms_cuentas.adapters.out.persistence.mapper.CuentaPersistenceMapper;
import com.ntt.ms_cuentas.adapters.out.persistence.repository.SpringDataCuentaRepository;
import com.ntt.ms_cuentas.application.port.out.CuentaRepositoryPort;
import com.ntt.ms_cuentas.domain.model.Cuenta;

@Component
public class CuentaJpaAdapter implements CuentaRepositoryPort {

	private final SpringDataCuentaRepository repository;
	private final CuentaPersistenceMapper mapper;

	public CuentaJpaAdapter(SpringDataCuentaRepository repository, CuentaPersistenceMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	@Override
	@Transactional
	public Cuenta save(Cuenta cuenta) {
		CuentaEntity saved = repository.save(mapper.toEntity(cuenta));
		return mapper.toDomain(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Cuenta> findById(UUID id) {
		return repository.findById(id).map(mapper::toDomain);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Cuenta> findByNumeroCuenta(String numeroCuenta) {
		return repository.findByNumeroCuenta(numeroCuenta).map(mapper::toDomain);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Cuenta> findAll() {
		return repository.findAll().stream().map(mapper::toDomain).toList();
	}

	@Override
	@Transactional
	public void deleteById(UUID id) {
		repository.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByNumeroCuenta(String numeroCuenta) {
		return repository.existsByNumeroCuenta(numeroCuenta);
	}
}
