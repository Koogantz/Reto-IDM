package com.idm.challenge.audit.domain.port.out;

import com.idm.challenge.audit.domain.entity.AuditRecord;
import reactor.core.publisher.Mono;

public interface AuditPersistencePort {

    Mono<AuditRecord> save(AuditRecord record);
}
