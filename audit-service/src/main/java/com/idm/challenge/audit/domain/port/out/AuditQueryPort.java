package com.idm.challenge.audit.domain.port.out;

import java.util.UUID;

import com.idm.challenge.audit.domain.entity.AuditRecord;

import reactor.core.publisher.Flux;

public interface AuditQueryPort {

    Flux<AuditRecord> findByTargetResourceId(UUID targetResourceId);
}
