package com.idm.challenge.audit.application.port.in;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterAuditCommand(
        @NotNull(message = "Target resource id is required")
        UUID targetResourceId,
        @NotBlank(message = "Action is required")
        String action,
        @NotBlank(message = "Payload is required")
        String payload
) {
}
