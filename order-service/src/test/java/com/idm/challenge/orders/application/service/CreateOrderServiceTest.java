package com.idm.challenge.orders.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.idm.challenge.orders.application.port.in.CreateOrderCommand;
import com.idm.challenge.orders.domain.entity.Order;
import com.idm.challenge.orders.domain.port.out.AuditLogPort;
import com.idm.challenge.orders.domain.port.out.OrderPersistencePort;
import com.idm.challenge.orders.domain.valueobject.OrderStatus;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CreateOrderServiceTest {

    @Mock
    private OrderPersistencePort persistencePort;

    @Mock
    private AuditLogPort auditLogPort;

    @Test
    void shouldPersistOrderAndSendAuditEvent() {
        CreateOrderService service = new CreateOrderService(persistencePort, auditLogPort);
        CreateOrderCommand command = new CreateOrderCommand("customer-123", new BigDecimal("125.45"), "USD");

        when(persistencePort.save(any(Order.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(auditLogPort.recordOrderCreated(any(Order.class))).thenReturn(Mono.empty());

        StepVerifier.create(service.createOrder(command))
                .assertNext(order -> {
                    assertThat(order.status()).isEqualTo(OrderStatus.CONFIRMED);
                    assertThat(order.totalAmount()).isEqualByComparingTo("125.45");
                })
                .verifyComplete();

        ArgumentCaptor<Order> auditCaptor = ArgumentCaptor.forClass(Order.class);
        verify(auditLogPort).recordOrderCreated(auditCaptor.capture());
        assertThat(auditCaptor.getValue().status()).isEqualTo(OrderStatus.CONFIRMED);
        verify(persistencePort, times(1)).save(any(Order.class));
    }
}
