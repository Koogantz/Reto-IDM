package com.idm.challenge.audit.infrastructure.controllers;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.idm.challenge.audit.application.dto.AuditRecordResponse;
import com.idm.challenge.audit.application.dto.RegisterAuditRequest;
import com.idm.challenge.audit.application.mapper.AuditRecordResponseMapper;
import com.idm.challenge.audit.application.port.in.RegisterAuditCommand;
import com.idm.challenge.audit.application.port.in.RegisterAuditUseCase;
import com.idm.challenge.audit.application.port.in.FindAuditRecordsUseCase;
import com.idm.challenge.audit.infrastructure.adapters.secret.SecretProvider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/audit")
@Validated
@Tag(name = "Auditoria", description = "Endpoints para gestionar registros de auditoria")
public class AuditController {

    private static final String API_KEY_HEADER = "X-API-Key";
    private static final String SECRET_KEY_NAME = "audit-service-api-key";

    private final RegisterAuditUseCase registerAuditUseCase;
    private final AuditRecordResponseMapper responseMapper;
    private final SecretProvider secretProvider;
    private final FindAuditRecordsUseCase findAuditRecordsUseCase;

    public AuditController(RegisterAuditUseCase registerAuditUseCase,
                           AuditRecordResponseMapper responseMapper,
                           SecretProvider secretProvider,
                           FindAuditRecordsUseCase findAuditRecordsUseCase) {
        this.registerAuditUseCase = registerAuditUseCase;
        this.responseMapper = responseMapper;
        this.secretProvider = secretProvider;
        this.findAuditRecordsUseCase = findAuditRecordsUseCase;
    }

        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        @Operation(
            summary = "Registrar un nuevo registro de auditoria",
            description = "Almacena un registro de auditoria despues de validar la clave API."
        )
        @ApiResponse(
            responseCode = "201",
            description = "Registro de auditoria creado correctamente",
            content = @Content(schema = @Schema(implementation = AuditRecordResponse.class))
        )
        @ApiResponse(
            responseCode = "401",
            description = "Clave API invalida",
            content = @Content
        )
        public Mono<AuditRecordResponse> register(
            @Parameter(name = API_KEY_HEADER, in = ParameterIn.HEADER, description = "Clave API requerida para autorizar la solicitud")
            @RequestHeader(API_KEY_HEADER) String apiKey,
            @Valid @RequestBody Mono<RegisterAuditRequest> request) {
        return validateApiKey(apiKey)
                .then(request.flatMap(body -> registerAuditUseCase.register(toCommand(body))))
                .map(responseMapper::toResponse);
    }

        @GetMapping("/{targetResourceId}")
        @Operation(summary = "Listar auditorias para un recurso")
        @ApiResponse(
            responseCode = "200",
            description = "Registros de auditoria obtenidos",
            content = @Content(schema = @Schema(implementation = AuditRecordResponse.class))
        )
        @ApiResponse(
            responseCode = "401",
            description = "Clave API invalida",
            content = @Content
        )
        public Flux<AuditRecordResponse> findByResource(
            @Parameter(name = API_KEY_HEADER, in = ParameterIn.HEADER, description = "Clave API requerida para autorizar la solicitud")
            @RequestHeader(API_KEY_HEADER) String apiKey,
            @PathVariable("targetResourceId") UUID targetResourceId) {
        // Reutiliza la validacion de la clave API antes de emitir los registros de auditoria.
        return validateApiKey(apiKey)
            .thenMany(findAuditRecordsUseCase.findByTargetResourceId(targetResourceId))
            .map(responseMapper::toResponse);
        }

    private Mono<Void> validateApiKey(String apiKey) {
        return Mono.fromRunnable(() -> {
            String expectedKey = secretProvider.getSecret(SECRET_KEY_NAME);
            if (!expectedKey.equals(apiKey)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Clave API invalida");
            }
        });
    }

    private RegisterAuditCommand toCommand(RegisterAuditRequest request) {
        return new RegisterAuditCommand(request.targetResourceId(), request.action(), request.payload());
    }
}
