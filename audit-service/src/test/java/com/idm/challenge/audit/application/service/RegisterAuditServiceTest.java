package com.idm.challenge.audit.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.idm.challenge.audit.application.port.in.RegisterAuditCommand;
import com.idm.challenge.audit.domain.entity.AuditRecord;
import com.idm.challenge.audit.domain.port.out.AuditPersistencePort;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class RegisterAuditServiceTest {

    @Mock
    private AuditPersistencePort persistencePort;

    @Test
    void shouldPersistAuditRecord() {
        RegisterAuditService service = new RegisterAuditService(persistencePort);
        RegisterAuditCommand command = new RegisterAuditCommand(
                UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                "CREATE_ORDER",
                "{\"sample\":true}");

        when(persistencePort.save(any(AuditRecord.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(service.register(command))
                .assertNext(record -> {
                    assertThat(record.targetResourceId()).isEqualTo(command.targetResourceId());
                    assertThat(record.action()).isEqualTo("CREATE_ORDER");
                })
                .verifyComplete();

        ArgumentCaptor<AuditRecord> captor = ArgumentCaptor.forClass(AuditRecord.class);
        verify(persistencePort).save(captor.capture());
        assertThat(captor.getValue().payload()).isEqualTo("{\"sample\":true}");
    }
}
