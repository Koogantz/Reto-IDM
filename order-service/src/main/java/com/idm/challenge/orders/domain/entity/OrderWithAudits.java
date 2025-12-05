package com.idm.challenge.orders.domain.entity;

import java.util.List;

public record OrderWithAudits(
        Order order,
        List<AuditRecord> audits
) {
}
