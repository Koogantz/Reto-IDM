package com.idm.challenge.audit.infrastructure.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.idm.challenge.audit.application.mapper.AuditRecordResponseMapper;
import com.idm.challenge.audit.application.port.in.FindAuditRecordsUseCase;
import com.idm.challenge.audit.application.port.in.RegisterAuditCommand;
import com.idm.challenge.audit.application.port.in.RegisterAuditUseCase;
import com.idm.challenge.audit.domain.entity.AuditRecord;
import com.idm.challenge.audit.infrastructure.adapters.secret.SecretProvider;
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

@WebFluxTest(controllers = AuditController.class)
@Import(AuditRecordResponseMapper.class)
class AuditControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private RegisterAuditUseCase registerAuditUseCase;

    @MockBean
    private SecretProvider secretProvider;

    @MockBean
    private FindAuditRecordsUseCase findAuditRecordsUseCase;

    @Test
    void shouldReturnCreatedWhenApiKeyValid() {
        AuditRecord storedRecord = new AuditRecord(
                UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                UUID.fromString("223e4567-e89b-12d3-a456-426614174000"),
                "CREATE_ORDER",
                "{\"order\":\"sample\"}",
                Instant.parse("2024-01-01T10:15:30Z"));

        when(secretProvider.getSecret("audit-service-api-key")).thenReturn("api-key");
        when(registerAuditUseCase.register(any(RegisterAuditCommand.class))).thenReturn(Mono.just(storedRecord));

        webTestClient.post()
                .uri("/api/audit")
                .header("X-API-Key", "api-key")
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .bodyValue("{" +
                        "\"targetResourceId\":\"223e4567-e89b-12d3-a456-426614174000\"," +
                        "\"action\":\"CREATE_ORDER\"," +
                        "\"payload\":\"{}\"}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("123e4567-e89b-12d3-a456-426614174000");
    }

    @Test
    void shouldReturnUnauthorizedWhenApiKeyInvalid() {
        when(secretProvider.getSecret("audit-service-api-key")).thenReturn("api-key");

        webTestClient.post()
                .uri("/api/audit")
                .header("X-API-Key", "wrong-key")
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .bodyValue("{" +
                        "\"targetResourceId\":\"223e4567-e89b-12d3-a456-426614174000\"," +
                        "\"action\":\"CREATE_ORDER\"," +
                        "\"payload\":\"{}\"}")
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
