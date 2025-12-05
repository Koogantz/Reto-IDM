package com.idm.challenge.orders.infrastructure.controllers;

import java.util.UUID;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.idm.challenge.orders.application.dto.CreateOrderRequest;
import com.idm.challenge.orders.application.dto.OrderResponse;
import com.idm.challenge.orders.application.dto.OrderWithAuditsResponse;
import com.idm.challenge.orders.application.mapper.OrderResponseMapper;
import com.idm.challenge.orders.application.mapper.OrderWithAuditsResponseMapper;
import com.idm.challenge.orders.application.port.in.CreateOrderCommand;
import com.idm.challenge.orders.application.port.in.CreateOrderUseCase;
import com.idm.challenge.orders.application.port.in.GetOrderWithAuditsUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/orders")
@Validated
@Tag(name = "Ordenes", description = "Endpoints para gestionar ordenes de clientes")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final OrderResponseMapper responseMapper;
    private final GetOrderWithAuditsUseCase getOrderWithAuditsUseCase;
    private final OrderWithAuditsResponseMapper orderWithAuditsResponseMapper;
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    public OrderController(CreateOrderUseCase createOrderUseCase,
            OrderResponseMapper responseMapper,
            GetOrderWithAuditsUseCase getOrderWithAuditsUseCase,
            OrderWithAuditsResponseMapper orderWithAuditsResponseMapper) {
        this.createOrderUseCase = createOrderUseCase;
        this.responseMapper = responseMapper;
        this.getOrderWithAuditsUseCase = getOrderWithAuditsUseCase;
        this.orderWithAuditsResponseMapper = orderWithAuditsResponseMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear una nueva orden")
    @ApiResponse(responseCode = "201", description = "Orden creada correctamente", content = @Content(schema = @Schema(implementation = OrderResponse.class)))
    public Mono<OrderResponse> create(@Valid @RequestBody Mono<CreateOrderRequest> request) {
        return request
                .doOnSubscribe(sub -> log.info("Solicitud de creacion de orden recibida"))
                .flatMap(payload -> {
                    log.debug("Creando orden para el cliente {}", payload.customerId());
                    return createOrderUseCase.createOrder(toCommand(payload));
                })
                .map(responseMapper::toResponse)
                .doOnSuccess(order -> log.info("Orden {} creada", order.id()));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Consultar una orden junto con sus auditorias")
    @ApiResponse(responseCode = "200", description = "Orden encontrada", content = @Content(schema = @Schema(implementation = OrderWithAuditsResponse.class)))
    @ApiResponse(responseCode = "404", description = "Orden no encontrada", content = @Content)
    // Devuelve la orden enriquecida con los registros de auditoria provenientes de audit-service.
    public Mono<OrderWithAuditsResponse> getOrder(
            @Parameter(name = "orderId", description = "Identificador de la orden", required = true) 
            @PathVariable("orderId") UUID orderId) {
        log.info("Consultando la orden {} con sus auditorias", orderId);
        return getOrderWithAuditsUseCase.getOrderWithAudits(orderId)
                .map(orderWithAuditsResponseMapper::toResponse)
                .doOnSuccess(
                        response -> log.debug("Orden {} devuelta con {} auditorias", orderId, response.audits().size()))
                .onErrorMap(IllegalArgumentException.class,
                        ex -> new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex));
    }

    private CreateOrderCommand toCommand(CreateOrderRequest request) {
        return new CreateOrderCommand(request.customerId(), request.totalAmount(), request.currency());
    }
}
