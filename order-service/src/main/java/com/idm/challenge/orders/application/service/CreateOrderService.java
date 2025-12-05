package com.idm.challenge.orders.application.service;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.idm.challenge.orders.application.port.in.CreateOrderCommand;
import com.idm.challenge.orders.application.port.in.CreateOrderUseCase;
import com.idm.challenge.orders.domain.entity.Order;
import com.idm.challenge.orders.domain.port.out.AuditLogPort;
import com.idm.challenge.orders.domain.port.out.OrderPersistencePort;

import reactor.core.publisher.Mono;

@Service
@Validated
public class CreateOrderService implements CreateOrderUseCase {

    private final OrderPersistencePort persistencePort;
    private final AuditLogPort auditLogPort;
    private static final Logger log = LoggerFactory.getLogger(CreateOrderService.class);

    public CreateOrderService(OrderPersistencePort persistencePort, AuditLogPort auditLogPort) {
        this.persistencePort = persistencePort;
        this.auditLogPort = auditLogPort;
    }

    @Override
    public Mono<Order> createOrder(@Valid CreateOrderCommand command) {
        Order confirmedOrder = Order.initialize(command.customerId(), command.totalAmount(), command.currency())
            .confirm();

        log.info("Confirmando la orden {} para el cliente {}", confirmedOrder.id(), confirmedOrder.customerId());
        // Persiste la orden y desencadena el envio de la auditoria una vez almacenada.
        return persistencePort.save(confirmedOrder)
            .doOnSuccess(order -> log.debug("Orden {} almacenada", order.id()))
            .delayUntil(order -> auditLogPort.recordOrderCreated(order)
                .doOnSubscribe(sub -> log.debug("Enviando auditoria para la orden {}", order.id())))
            .doOnError(error -> log.error("Error al crear la orden {}", confirmedOrder.id(), error));
    }
}
