package com.idm.challenge.audit.application.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload to register a new audit record")
public record RegisterAuditRequest(
        @NotNull(message = "targetResourceId is required")
        @Schema(description = "Identifier of the resource that was affected", example = "1f4d91f0-2e9a-4c3f-8bc9-4d9e2b0df314")
        UUID targetResourceId,
        @NotBlank(message = "action is required")
        @Schema(description = "Action performed over the resource", example = "ORDER_CREATED")
        String action,
        @NotBlank(message = "payload is required")
        @Schema(description = "Serialized payload detailing the event", example = "{\"orderId\":\"123\"}")
        String payload
) {
}
