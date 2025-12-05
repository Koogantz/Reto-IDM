package com.idm.challenge.orders.application.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload required to create an order")
public record CreateOrderRequest(
        @NotBlank(message = "customerId is required")
        @Schema(description = "Customer identifier", example = "customer-123")
        String customerId,
        @NotNull(message = "totalAmount is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "totalAmount must be positive")
        @Digits(integer = 15, fraction = 2, message = "totalAmount must have a maximum of 15 digits and 2 decimals")
        @Schema(description = "Total amount of the order", example = "120.50")
        BigDecimal totalAmount,
        @NotBlank(message = "currency is required")
        @Schema(description = "Currency ISO code", example = "USD")
        String currency
) {
}
