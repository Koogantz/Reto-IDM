package com.idm.challenge.audit.application.service;

import jakarta.validation.Valid;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.idm.challenge.audit.application.port.in.RegisterAuditCommand;
import com.idm.challenge.audit.application.port.in.RegisterAuditUseCase;
import com.idm.challenge.audit.domain.entity.AuditRecord;
import com.idm.challenge.audit.domain.port.out.AuditPersistencePort;

import reactor.core.publisher.Mono;

@Service
@Validated
public class RegisterAuditService implements RegisterAuditUseCase {

    private final AuditPersistencePort persistencePort;

    public RegisterAuditService(AuditPersistencePort persistencePort) {
        this.persistencePort = persistencePort;
    }

    @Override
    public Mono<AuditRecord> register(@Valid RegisterAuditCommand command) {
        AuditRecord record = AuditRecord.create(command.targetResourceId(), command.action(), command.payload());
        return persistencePort.save(record);
    }
}
