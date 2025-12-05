package com.idm.challenge.orders.infrastructure.adapters.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.idm.challenge.orders.domain.entity.Order;
import com.idm.challenge.orders.domain.valueobject.OrderStatus;
import com.idm.challenge.orders.infrastructure.adapters.persistence.entity.OrderEntity;

@Mapper(componentModel = "spring")
public interface OrderEntityMapper {

    @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
    OrderEntity toEntity(Order order);

    @Mapping(target = "status", source = "status", qualifiedByName = "stringToStatus")
    Order toDomain(OrderEntity entity);

    @Named("statusToString")
    default String statusToString(OrderStatus status) {
        return status.name();
    }

    @Named("stringToStatus")
    default OrderStatus stringToStatus(String status) {
        return OrderStatus.valueOf(status);
    }
}
