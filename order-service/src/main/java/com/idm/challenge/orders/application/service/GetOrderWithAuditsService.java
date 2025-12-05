package com.idm.challenge.orders.application.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.idm.challenge.orders.application.port.in.GetOrderWithAuditsUseCase;
import com.idm.challenge.orders.domain.entity.OrderWithAudits;
import com.idm.challenge.orders.domain.port.out.AuditLookupPort;
import com.idm.challenge.orders.domain.port.out.OrderPersistencePort;

import reactor.core.publisher.Mono;

@Service
public class GetOrderWithAuditsService implements GetOrderWithAuditsUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetOrderWithAuditsService.class);
    private final OrderPersistencePort orderPersistencePort;
    private final AuditLookupPort auditLookupPort;

    public GetOrderWithAuditsService(OrderPersistencePort orderPersistencePort,
                                     AuditLookupPort auditLookupPort) {
        this.orderPersistencePort = orderPersistencePort;
        this.auditLookupPort = auditLookupPort;
    }

    @Override
    public Mono<OrderWithAudits> getOrderWithAudits(UUID orderId) {
        log.info("Recuperando la orden {} con sus auditorias", orderId);
        // Compone la respuesta de la orden junto con la traza de auditorias.
        return orderPersistencePort.findById(orderId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Orden %s no encontrada".formatted(orderId))))
                .flatMap(order -> auditLookupPort.findByTargetResourceId(orderId)
                        .collectList()
                        .map(audits -> new OrderWithAudits(order, audits)))
            .doOnSuccess(result -> {
                if (result != null) {
                log.debug("Se encontraron {} registros de auditoria para la orden {}", result.audits().size(), orderId);
                }
            })
            .doOnError(error -> log.error("Error al obtener las auditorias de la orden {}", orderId, error));
    }
}
