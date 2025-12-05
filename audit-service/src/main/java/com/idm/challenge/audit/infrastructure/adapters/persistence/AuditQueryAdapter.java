package com.idm.challenge.audit.infrastructure.adapters.persistence;

import java.util.Objects;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.idm.challenge.audit.domain.entity.AuditRecord;
import com.idm.challenge.audit.domain.port.out.AuditQueryPort;
import com.idm.challenge.audit.infrastructure.adapters.persistence.mapper.AuditRecordEntityMapper;
import com.idm.challenge.audit.infrastructure.adapters.persistence.repository.ReactiveAuditRecordRepository;

import reactor.core.publisher.Flux;

@Component
public class AuditQueryAdapter implements AuditQueryPort {

    private static final Logger log = LoggerFactory.getLogger(AuditQueryAdapter.class);
    private final ReactiveAuditRecordRepository repository;
    private final AuditRecordEntityMapper mapper;

    public AuditQueryAdapter(ReactiveAuditRecordRepository repository,
                             AuditRecordEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Flux<AuditRecord> findByTargetResourceId(UUID targetResourceId) {
        UUID lookupId = Objects.requireNonNull(targetResourceId, "Target resource id must not be null");
        log.debug("Loading audit entries for resource {}", lookupId);
        return repository.findByTargetResourceId(lookupId)
                .map(mapper::toDomain)
                .doOnComplete(() -> log.debug("Audit load finished for resource {}", lookupId));
    }
}
