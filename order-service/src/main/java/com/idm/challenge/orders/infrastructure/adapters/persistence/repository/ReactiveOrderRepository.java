package com.idm.challenge.orders.infrastructure.adapters.persistence.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.idm.challenge.orders.infrastructure.adapters.persistence.entity.OrderEntity;

public interface ReactiveOrderRepository extends ReactiveCrudRepository<OrderEntity, UUID> {
}
