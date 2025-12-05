package com.idm.challenge.orders.domain.port.out;

import com.idm.challenge.orders.domain.entity.Order;

import reactor.core.publisher.Mono;

public interface AuditLogPort {

    Mono<Void> recordOrderCreated(Order order);
}
