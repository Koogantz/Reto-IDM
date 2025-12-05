package com.idm.challenge.orders.infrastructure.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.idm.challenge.orders.application.mapper.OrderResponseMapper;
import com.idm.challenge.orders.application.port.in.CreateOrderCommand;
import com.idm.challenge.orders.application.port.in.CreateOrderUseCase;
import com.idm.challenge.orders.application.port.in.GetOrderWithAuditsUseCase;
import com.idm.challenge.orders.domain.entity.Order;
import com.idm.challenge.orders.domain.valueobject.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = OrderController.class)
@Import({OrderResponseMapper.class, com.idm.challenge.orders.application.mapper.OrderWithAuditsResponseMapper.class})
class OrderControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CreateOrderUseCase createOrderUseCase;

        @MockBean
        private GetOrderWithAuditsUseCase getOrderWithAuditsUseCase;

    @Test
    void shouldReturnCreatedOrder() {
        Order confirmedOrder = new Order(
                UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                "customer-123",
                new BigDecimal("23.45"),
                "USD",
                OrderStatus.CONFIRMED,
                Instant.parse("2024-01-01T10:15:30Z"));

        when(createOrderUseCase.createOrder(any(CreateOrderCommand.class))).thenReturn(Mono.just(confirmedOrder));

        webTestClient.post()
                .uri("/api/orders")
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .bodyValue("{" +
                        "\"customerId\":\"customer-123\"," +
                        "\"totalAmount\":23.45," +
                        "\"currency\":\"USD\"}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("123e4567-e89b-12d3-a456-426614174000")
                .jsonPath("$.status").isEqualTo("CONFIRMED");
    }

    @Test
    void shouldValidateInputPayload() {
        webTestClient.post()
                .uri("/api/orders")
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .bodyValue("{" +
                        "\"customerId\":\"\"," +
                        "\"totalAmount\":-5," +
                        "\"currency\":\"\"}")
                .exchange()
                .expectStatus().isBadRequest();
    }
}
