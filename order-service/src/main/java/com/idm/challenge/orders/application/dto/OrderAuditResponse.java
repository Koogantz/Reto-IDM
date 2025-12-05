package com.idm.challenge.orders.application.dto;

import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Audit record associated with an order")
public record OrderAuditResponse(
        @Schema(description = "Audit identifier", example = "71dc4f87-4c7c-4a39-86b6-30da307c5cf7")
        UUID id,
        @Schema(description = "Order identifier the audit belongs to", example = "a7f5f5e3-962d-4f37-9f4e-18a675d023b4")
        UUID targetResourceId,
        @Schema(description = "Action captured for the order", example = "CREATE_ORDER")
        String action,
        @Schema(description = "Captured payload for diagnostic purposes")
        String payload,
        @Schema(description = "Timestamp when the audit was stored")
        Instant timestamp
) {
}
