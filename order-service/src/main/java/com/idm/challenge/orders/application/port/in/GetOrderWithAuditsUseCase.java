package com.idm.challenge.orders.application.port.in;

import java.util.UUID;

import com.idm.challenge.orders.domain.entity.OrderWithAudits;

import reactor.core.publisher.Mono;

public interface GetOrderWithAuditsUseCase {

    Mono<OrderWithAudits> getOrderWithAudits(UUID orderId);
}
