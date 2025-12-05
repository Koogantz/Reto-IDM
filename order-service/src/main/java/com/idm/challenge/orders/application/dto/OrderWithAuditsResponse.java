package com.idm.challenge.orders.application.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Order enriched with audit entries")
public record OrderWithAuditsResponse(
        @Schema(description = "Order data")
        OrderResponse order,
        @Schema(description = "Audits linked to the order")
        List<OrderAuditResponse> audits
) {
}
