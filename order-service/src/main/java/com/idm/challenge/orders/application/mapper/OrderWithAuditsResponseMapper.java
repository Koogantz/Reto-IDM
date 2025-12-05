package com.idm.challenge.orders.application.mapper;

import org.springframework.stereotype.Component;

import com.idm.challenge.orders.application.dto.OrderAuditResponse;
import com.idm.challenge.orders.application.dto.OrderWithAuditsResponse;
import com.idm.challenge.orders.domain.entity.AuditRecord;
import com.idm.challenge.orders.domain.entity.OrderWithAudits;

@Component
public class OrderWithAuditsResponseMapper {

    private final OrderResponseMapper orderResponseMapper;

    public OrderWithAuditsResponseMapper(OrderResponseMapper orderResponseMapper) {
        this.orderResponseMapper = orderResponseMapper;
    }

    public OrderWithAuditsResponse toResponse(OrderWithAudits aggregate) {
        return new OrderWithAuditsResponse(
                orderResponseMapper.toResponse(aggregate.order()),
                aggregate.audits().stream().map(this::toAuditResponse).toList()
        );
    }

    private OrderAuditResponse toAuditResponse(AuditRecord record) {
        return new OrderAuditResponse(record.id(), record.targetResourceId(), record.action(), record.payload(), record.timestamp());
    }
}
