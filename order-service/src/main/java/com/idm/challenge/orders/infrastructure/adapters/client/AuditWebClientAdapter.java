package com.idm.challenge.orders.infrastructure.adapters.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.idm.challenge.orders.domain.entity.AuditRecord;
import com.idm.challenge.orders.domain.entity.Order;
import com.idm.challenge.orders.domain.port.out.AuditLogPort;
import com.idm.challenge.orders.domain.port.out.AuditLookupPort;
import com.idm.challenge.orders.infrastructure.adapters.secret.SecretProvider;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class AuditWebClientAdapter implements AuditLogPort, AuditLookupPort {

    private static final String AUDIT_ACTION = "CREATE_ORDER";
    private static final String AUDIT_ENDPOINT = "/api/audit";
    private static final String API_KEY_HEADER = "X-API-Key";
    private static final Logger log = LoggerFactory.getLogger(AuditWebClientAdapter.class);

    private final WebClient.Builder webClientBuilder;
    private final SecretProvider secretProvider;
    private final ObjectMapper objectMapper;

    public AuditWebClientAdapter(WebClient.Builder webClientBuilder,
                                 SecretProvider secretProvider,
                                 ObjectMapper objectMapper) {
        this.webClientBuilder = webClientBuilder;
        this.secretProvider = secretProvider;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> recordOrderCreated(Order order) {
        return Mono.defer(() -> Mono.fromSupplier(() -> serializeOrder(order))
            .flatMap(payload -> buildClient()
                        .post()
                        .uri(AUDIT_ENDPOINT)
                        .header(API_KEY_HEADER, resolveSecret("audit-service-api-key"))
                        .bodyValue(new AuditClientRequest(order.id(), AUDIT_ACTION, payload))
                        .retrieve()
            .onStatus(HttpStatusCode::isError, this::mapError)
                .bodyToMono(Void.class)))
            .doOnSubscribe(subscription -> log.info("Enviando auditoria para la orden {}", order.id()))
            .doOnSuccess(ignored -> log.debug("Auditoria registrada para la orden {}", order.id()))
            .doOnError(error -> log.error("Fallo la llamada de auditoria para la orden {}", order.id(), error));
    }

        @Override
        public Flux<AuditRecord> findByTargetResourceId(java.util.UUID targetResourceId) {
        log.debug("Solicitando auditorias para la orden {}", targetResourceId);
            // Invoca la API HTTP de auditoria para listar los registros asociados a la orden.
        return buildClient()
            .get()
            .uri(AUDIT_ENDPOINT + "/" + targetResourceId)
            .header(API_KEY_HEADER, resolveSecret("audit-service-api-key"))
            .retrieve()
            .onStatus(HttpStatusCode::isError, this::mapError)
            .bodyToFlux(AuditClientResponse.class)
            .map(this::mapToDomain)
            .doOnComplete(() -> log.debug("Finalizo la consulta de auditorias para la orden {}", targetResourceId))
            .doOnError(error -> log.error("Fallo la consulta de auditorias para la orden {}", targetResourceId, error));
        }

    private WebClient buildClient() {
        String baseUrl = Objects.requireNonNull(resolveSecret("audit-service-base-url"), "Audit base url must not be null");
        return webClientBuilder.clone()
                .baseUrl(baseUrl)
                .build();
    }

    private String resolveSecret(String key) {
        return secretProvider.getSecret(key);
    }

    private Mono<Throwable> mapError(ClientResponse response) {
        return response.bodyToMono(String.class)
                .defaultIfEmpty("El servicio de auditoria respondio con estado " + response.statusCode())
                .map(body -> new IllegalStateException("Error del servicio de auditoria: " + body));
    }

    private String serializeOrder(Order order) {
        try {
            return objectMapper.writeValueAsString(order);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("No se pudo serializar el payload de la orden", exception);
        }
    }

    private AuditRecord mapToDomain(AuditClientResponse response) {
        return new AuditRecord(response.id(), response.targetResourceId(), response.action(), response.payload(), response.timestamp());
    }

    private record AuditClientResponse(java.util.UUID id, java.util.UUID targetResourceId,
            String action, String payload, java.time.Instant timestamp) {
    }
}
