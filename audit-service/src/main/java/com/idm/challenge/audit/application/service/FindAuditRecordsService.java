package com.idm.challenge.audit.application.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.idm.challenge.audit.application.port.in.FindAuditRecordsUseCase;
import com.idm.challenge.audit.domain.entity.AuditRecord;
import com.idm.challenge.audit.domain.port.out.AuditQueryPort;

import reactor.core.publisher.Flux;

@Service
public class FindAuditRecordsService implements FindAuditRecordsUseCase {

    private static final Logger log = LoggerFactory.getLogger(FindAuditRecordsService.class);
    private final AuditQueryPort auditQueryPort;

    public FindAuditRecordsService(AuditQueryPort auditQueryPort) {
        this.auditQueryPort = auditQueryPort;
    }

    @Override
    public Flux<AuditRecord> findByTargetResourceId(UUID targetResourceId) {
        log.info("Listando auditorias para el recurso {}", targetResourceId);
        // Delega en el adaptador de persistencia para obtener los registros de auditoria.
        return auditQueryPort.findByTargetResourceId(targetResourceId)
            .doOnError(error -> log.error("Fallo al listar las auditorias del recurso {}", targetResourceId, error));
    }
}
