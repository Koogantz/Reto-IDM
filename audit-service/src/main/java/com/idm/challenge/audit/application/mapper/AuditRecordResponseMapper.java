package com.idm.challenge.audit.application.mapper;

import org.springframework.stereotype.Component;

import com.idm.challenge.audit.application.dto.AuditRecordResponse;
import com.idm.challenge.audit.domain.entity.AuditRecord;

@Component
public class AuditRecordResponseMapper {

    public AuditRecordResponse toResponse(AuditRecord record) {
        return new AuditRecordResponse(
                record.id(),
                record.targetResourceId(),
                record.action(),
            record.payload(),
                record.timestamp()
        );
    }
}
