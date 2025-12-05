package com.idm.challenge.orders.application.port.in;

import jakarta.validation.Valid;

import org.springframework.validation.annotation.Validated;

import com.idm.challenge.orders.domain.entity.Order;

import reactor.core.publisher.Mono;

@Validated
public interface CreateOrderUseCase {

    Mono<Order> createOrder(@Valid CreateOrderCommand command);
}
