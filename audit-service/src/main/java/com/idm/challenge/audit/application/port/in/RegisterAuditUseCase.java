package com.idm.challenge.audit.application.port.in;

import jakarta.validation.Valid;

import org.springframework.validation.annotation.Validated;

import com.idm.challenge.audit.domain.entity.AuditRecord;

import reactor.core.publisher.Mono;

@Validated
public interface RegisterAuditUseCase {

    Mono<AuditRecord> register(@Valid RegisterAuditCommand command);
}
