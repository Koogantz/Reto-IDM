package com.idm.challenge.orders.infrastructure.adapters.persistence.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("orders")
public class OrderEntity {

    @Id
    private UUID id;

    @Column("customer_id")
    private String customerId;

    @Column("total_amount")
    private BigDecimal totalAmount;

    @Column("currency")
    private String currency;

    @Column("status")
    private String status;

    @Column("created_at")
    private Instant createdAt;
}
