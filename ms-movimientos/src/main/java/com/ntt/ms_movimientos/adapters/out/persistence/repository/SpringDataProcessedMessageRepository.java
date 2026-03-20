package com.ntt.ms_movimientos.adapters.out.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ntt.ms_movimientos.adapters.out.persistence.entity.ProcessedMessageEntity;

public interface SpringDataProcessedMessageRepository extends JpaRepository<ProcessedMessageEntity, UUID> {

	Optional<ProcessedMessageEntity> findByConsumerAndMessageId(String consumer, UUID messageId);
}
