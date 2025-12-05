package com.idm.challenge.orders.domain.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.idm.challenge.orders.domain.valueobject.OrderStatus;

public record Order(
        @NotNull(message = "Order id is required")
        UUID id,
        @NotBlank(message = "Customer id is required")
        String customerId,
        @NotNull(message = "Total amount is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be positive")
        @Digits(integer = 15, fraction = 2, message = "Total amount must have a maximum of 15 digits and 2 decimals")
        BigDecimal totalAmount,
        @NotBlank(message = "Currency is required")
        String currency,
        @NotNull(message = "Status is required")
        OrderStatus status,
        @NotNull(message = "Creation timestamp is required")
        Instant createdAt
) {

    public static Order initialize(String customerId, BigDecimal totalAmount, String currency) {
        return new Order(UUID.randomUUID(), customerId, totalAmount, currency, OrderStatus.PENDING, Instant.now());
    }

    public Order confirm() {
        return new Order(id, customerId, totalAmount, currency, OrderStatus.CONFIRMED, createdAt);
    }
}
