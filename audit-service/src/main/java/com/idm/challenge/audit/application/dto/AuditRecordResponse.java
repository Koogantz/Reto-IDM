package com.idm.challenge.audit.application.dto;

import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Audit record representation")
public record AuditRecordResponse(
        @Schema(description = "Audit identifier", example = "4e0c9b6d-2eb0-4c1b-86c3-9afd3b5a23bf")
        UUID id,
        @Schema(description = "Identifier of the resource that was affected", example = "1f4d91f0-2e9a-4c3f-8bc9-4d9e2b0df314")
        UUID targetResourceId,
        @Schema(description = "Action performed over the resource", example = "ORDER_CREATED")
        String action,
        @Schema(description = "Payload captured when the audit was stored")
        String payload,
        @Schema(description = "Instant when the audit was captured")
        Instant timestamp
) {
}
