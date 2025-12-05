package com.idm.challenge.audit.domain.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuditRecord(
        @NotNull(message = "id is required")
        UUID id,
        @NotNull(message = "targetResourceId is required")
        UUID targetResourceId,
        @NotBlank(message = "action is required")
        String action,
        @NotBlank(message = "payload is required")
        String payload,
        @NotNull(message = "timestamp is required")
        Instant timestamp
) {

    public static AuditRecord create(UUID targetResourceId, String action, String payload) {
        return new AuditRecord(UUID.randomUUID(), targetResourceId, action, payload, Instant.now());
    }
}
