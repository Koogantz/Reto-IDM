package com.idm.challenge.audit.infrastructure.adapters.persistence.mapper;

import org.mapstruct.Mapper;

import com.idm.challenge.audit.domain.entity.AuditRecord;
import com.idm.challenge.audit.infrastructure.adapters.persistence.entity.AuditRecordEntity;

@Mapper(componentModel = "spring")
public interface AuditRecordEntityMapper {

    AuditRecordEntity toEntity(AuditRecord domain);

    AuditRecord toDomain(AuditRecordEntity entity);
}
