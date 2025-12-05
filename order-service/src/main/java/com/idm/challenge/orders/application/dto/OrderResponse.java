package com.idm.challenge.orders.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Order representation returned by the service")
public record OrderResponse(
        @Schema(description = "Order identifier", example = "a7f5f5e3-962d-4f37-9f4e-18a675d023b4")
        UUID id,
        @Schema(description = "Customer identifier", example = "customer-123")
        String customerId,
        @Schema(description = "Total amount of the order", example = "120.50")
        BigDecimal totalAmount,
        @Schema(description = "Currency ISO code", example = "USD")
        String currency,
        @Schema(description = "Current order status", example = "CONFIRMED")
        String status,
        @Schema(description = "Instant when the order was created")
        Instant createdAt
) {
}
