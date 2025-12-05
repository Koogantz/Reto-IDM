package com.idm.challenge.orders.domain.port.out;

import java.util.UUID;

import com.idm.challenge.orders.domain.entity.AuditRecord;

import reactor.core.publisher.Flux;

public interface AuditLookupPort {

    Flux<AuditRecord> findByTargetResourceId(UUID targetResourceId);
}
