package com.idm.challenge.orders.application.mapper;

import org.springframework.stereotype.Component;

import com.idm.challenge.orders.application.dto.OrderResponse;
import com.idm.challenge.orders.domain.entity.Order;

@Component
public class OrderResponseMapper {

    public OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.id(),
                order.customerId(),
                order.totalAmount(),
                order.currency(),
                order.status().name(),
                order.createdAt()
        );
    }
}
