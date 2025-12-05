package com.idm.challenge.audit.infrastructure.adapters.persistence.entity;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("audit_records")
public class AuditRecordEntity {

    @Id
    private UUID id;

    @Column("target_resource_id")
    private UUID targetResourceId;

    @Column("action")
    private String action;

    @Column("payload")
    private String payload;

    @Column("timestamp")
    private Instant timestamp;
}
