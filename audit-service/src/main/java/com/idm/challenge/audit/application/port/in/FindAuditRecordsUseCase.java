package com.idm.challenge.audit.application.port.in;

import java.util.UUID;

import com.idm.challenge.audit.domain.entity.AuditRecord;

import reactor.core.publisher.Flux;

public interface FindAuditRecordsUseCase {

    Flux<AuditRecord> findByTargetResourceId(UUID targetResourceId);
}
