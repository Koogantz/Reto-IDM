package com.idm.challenge.orders.domain.entity;

import java.time.Instant;
import java.util.UUID;

public record AuditRecord(
        UUID id,
        UUID targetResourceId,
        String action,
        String payload,
        Instant timestamp
) {
}
