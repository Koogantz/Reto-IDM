package com.idm.challenge.orders.infrastructure.adapters.client;

import java.util.UUID;

public record AuditClientRequest(
        UUID targetResourceId,
        String action,
        String payload
) {
}
