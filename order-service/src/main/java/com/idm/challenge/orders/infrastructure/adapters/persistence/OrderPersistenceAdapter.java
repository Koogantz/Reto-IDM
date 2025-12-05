package com.idm.challenge.orders.infrastructure.adapters.persistence;

import java.util.Objects;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Component;

import com.idm.challenge.orders.domain.entity.Order;
import com.idm.challenge.orders.domain.port.out.OrderPersistencePort;
import com.idm.challenge.orders.infrastructure.adapters.persistence.entity.OrderEntity;
import com.idm.challenge.orders.infrastructure.adapters.persistence.mapper.OrderEntityMapper;
import com.idm.challenge.orders.infrastructure.adapters.persistence.repository.ReactiveOrderRepository;

import reactor.core.publisher.Mono;

@Component
public class OrderPersistenceAdapter implements OrderPersistencePort {

    private final ReactiveOrderRepository repository;
    private final OrderEntityMapper mapper;
    private final R2dbcEntityTemplate entityTemplate;
    private static final Logger log = LoggerFactory.getLogger(OrderPersistenceAdapter.class);

    public OrderPersistenceAdapter(ReactiveOrderRepository repository, OrderEntityMapper mapper,
                                   R2dbcEntityTemplate entityTemplate) {
        this.repository = repository;
        this.mapper = mapper;
        this.entityTemplate = entityTemplate;
    }

    @Override
    public Mono<Order> save(Order order) {
        OrderEntity entity = Objects.requireNonNull(mapper.toEntity(order), "Order entity must not be null");
        UUID entityId = Objects.requireNonNull(entity.getId(), "Order id must not be null");

        log.debug("Persistiendo la orden {}", entityId);
        // Guarda filas existentes o inserta nuevos registros mediante el template cuando no existen.
        return repository.existsById(entityId)
                .flatMap(exists -> exists
                        ? repository.save(entity)
                : entityTemplate.insert(OrderEntity.class).using(entity))
            .map(mapper::toDomain)
            .doOnSuccess(saved -> log.debug("Orden {} persistida", saved.id()));
    }

    @Override
    public Mono<Order> findById(UUID orderId) {
        UUID queryId = Objects.requireNonNull(orderId, "Order id must not be null");
        log.debug("Buscando la orden {}", queryId);
        return repository.findById(queryId)
            .map(mapper::toDomain)
            .doOnNext(order -> log.debug("Orden {} cargada", order.id()));
    }
}
