package com.idm.challenge.audit.infrastructure.adapters.persistence.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;

import com.idm.challenge.audit.infrastructure.adapters.persistence.entity.AuditRecordEntity;

public interface ReactiveAuditRecordRepository extends ReactiveCrudRepository<AuditRecordEntity, UUID> {

	Flux<AuditRecordEntity> findByTargetResourceId(UUID targetResourceId);
}
