package com.idm.challenge.orders.domain.port.out;

import com.idm.challenge.orders.domain.entity.Order;
import java.util.UUID;

import reactor.core.publisher.Mono;

public interface OrderPersistencePort {

    Mono<Order> save(Order order);

    Mono<Order> findById(UUID orderId);
}
