package com.idm.challenge.orders.application.port.in;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateOrderCommand(
        @NotBlank(message = "Customer id is required")
        String customerId,
        @NotNull(message = "Total amount is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be positive")
        @Digits(integer = 15, fraction = 2, message = "Total amount must have a maximum of 15 digits and 2 decimals")
        BigDecimal totalAmount,
        @NotBlank(message = "Currency is required")
        String currency
) {
}
