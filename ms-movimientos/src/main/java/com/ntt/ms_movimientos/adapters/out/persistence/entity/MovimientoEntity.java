package com.ntt.ms_movimientos.adapters.out.persistence.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.ntt.ms_movimientos.domain.model.EstadoMovimiento;
import com.ntt.ms_movimientos.domain.model.TipoMovimiento;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "movimiento")
public class MovimientoEntity {

	@Id
	@Column(name = "id", columnDefinition = "uuid")
	private UUID id;

	@Column(name = "cuenta_id", nullable = false, columnDefinition = "uuid")
	private UUID cuentaId;

	@Column(name = "fecha", nullable = false)
	private Instant fecha;

	@Enumerated(EnumType.STRING)
	@Column(name = "tipo", nullable = false, length = 20)
	private TipoMovimiento tipo;

	@Column(name = "valor", nullable = false, precision = 19, scale = 2)
	private BigDecimal valor;

	@Column(name = "saldo_disponible", precision = 19, scale = 2)
	private BigDecimal saldoDisponible;

	@Enumerated(EnumType.STRING)
	@Column(name = "estado", nullable = false, length = 20)
	private EstadoMovimiento estado;

	@Column(name = "saga_id", columnDefinition = "uuid")
	private UUID sagaId;

	@Column(name = "command_id", columnDefinition = "uuid", unique = true)
	private UUID commandId;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@PrePersist
	void prePersist() {
		if (id == null) {
			id = UUID.randomUUID();
		}
		if (createdAt == null) {
			createdAt = Instant.now();
		}
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getCuentaId() {
		return cuentaId;
	}

	public void setCuentaId(UUID cuentaId) {
		this.cuentaId = cuentaId;
	}

	public Instant getFecha() {
		return fecha;
	}

	public void setFecha(Instant fecha) {
		this.fecha = fecha;
	}

	public TipoMovimiento getTipo() {
		return tipo;
	}

	public void setTipo(TipoMovimiento tipo) {
		this.tipo = tipo;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public BigDecimal getSaldoDisponible() {
		return saldoDisponible;
	}

	public void setSaldoDisponible(BigDecimal saldoDisponible) {
		this.saldoDisponible = saldoDisponible;
	}

	public EstadoMovimiento getEstado() {
		return estado;
	}

	public void setEstado(EstadoMovimiento estado) {
		this.estado = estado;
	}

	public UUID getSagaId() {
		return sagaId;
	}

	public void setSagaId(UUID sagaId) {
		this.sagaId = sagaId;
	}

	public UUID getCommandId() {
		return commandId;
	}

	public void setCommandId(UUID commandId) {
		this.commandId = commandId;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}
}
