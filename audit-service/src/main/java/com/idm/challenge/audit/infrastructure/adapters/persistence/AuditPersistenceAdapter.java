package com.idm.challenge.audit.infrastructure.adapters.persistence;

import java.util.Objects;
import java.util.UUID;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Component;

import com.idm.challenge.audit.domain.entity.AuditRecord;
import com.idm.challenge.audit.domain.port.out.AuditPersistencePort;
import com.idm.challenge.audit.infrastructure.adapters.persistence.entity.AuditRecordEntity;
import com.idm.challenge.audit.infrastructure.adapters.persistence.mapper.AuditRecordEntityMapper;
import reactor.core.publisher.Mono;

@Component
public class AuditPersistenceAdapter implements AuditPersistencePort {

    private final AuditRecordEntityMapper mapper;
    private final R2dbcEntityTemplate entityTemplate;

    public AuditPersistenceAdapter(AuditRecordEntityMapper mapper, R2dbcEntityTemplate entityTemplate) {
        this.mapper = mapper;
        this.entityTemplate = entityTemplate;
    }

    @Override
    public Mono<AuditRecord> save(AuditRecord record) {
        var entity = Objects.requireNonNull(mapper.toEntity(record), "Audit record entity must not be null");
        UUID entityId = Objects.requireNonNull(entity.getId(), "Audit record id must not be null");

        return entityTemplate.insert(AuditRecordEntity.class)
                .using(entity)
                .filter(saved -> entityId.equals(saved.getId()))
                .switchIfEmpty(Mono.error(new IllegalStateException("Failed to persist audit record " + entityId)))
                .map(mapper::toDomain);
    }
}
