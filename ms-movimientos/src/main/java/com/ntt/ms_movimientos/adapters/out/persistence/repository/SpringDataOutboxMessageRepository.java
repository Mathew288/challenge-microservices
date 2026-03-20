package com.ntt.ms_movimientos.adapters.out.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ntt.ms_movimientos.adapters.out.persistence.entity.OutboxMessageEntity;

public interface SpringDataOutboxMessageRepository extends JpaRepository<OutboxMessageEntity, UUID> {

	List<OutboxMessageEntity> findByStatusOrderByCreatedAtAsc(OutboxMessageEntity.Status status, Pageable pageable);
}
