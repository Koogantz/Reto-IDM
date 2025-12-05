# IDM Technical Challenge

Solución propuesta para el reto backend senior con dos microservicios reactivos basados en Spring Boot 3.3 y Java 17. Ambos servicios siguen arquitectura hexagonal, utilizan R2DBC con H2 en memoria, documentan la API vía OpenAPI y encapsulan secretos detrás de un `SecretProvider` con integración lista para Azure Key Vault.

## Módulos

- `order-service`: expone la creación de órdenes, persiste el estado y envía eventos al servicio de auditoría mediante `WebClient` no bloqueante.
- `audit-service`: recibe y persiste auditorías con un payload JSON de las acciones sobre órdenes.

Cada servicio define capas `domain`, `application` y `infrastructure` con puertos y adaptadores para respetar el estilo hexagonal.

## Tecnologias principales

- Spring Boot 3.3 (WebFlux, Actuator, Validation)
- Spring Data R2DBC + H2 en memoria
- Reactor y programación funcional (Mono/Flux, records, enums)
- MapStruct + Lombok para mappers
- springdoc-openapi para documentación interactiva (`/swagger-ui.html`)
- Azure Key Vault SDK (`azure-security-keyvault-secrets`, `azure-identity`)
- JUnit 5, Mockito, Reactor Test

## Gestión de secretos (Key Vault Offline Friendly)

Se definió el puerto `SecretProvider` en ambos servicios:

- Perfil `local`/`test`: `LocalSecretProvider` lee secretos desde `application.yml` o variables de entorno, permitiendo ejecución sin dependencia de Azure.
- Perfil `prod`: `AzureKeyVaultSecretProvider` usa `SecretClient` y `DefaultAzureCredential`. El bean solo se inicializa cuando el perfil `prod` está activo; en local no se requieren credenciales ni conexión a Azure.

Para Docker Compose o despliegues locales, basta con definir variables de entorno `IDM_LOCAL_SECRETS_*` (ver sección de ejecución).

## Ejecucion local

### Prerrequisitos

- JDK 17
- Maven 3.9+
- Docker 24+ (opcional, para orquestar con Compose)

### Pasos manuales (Maven + Java)

1. Abre PowerShell en la carpeta `Reto IDM`.
2. Compila ambos microservicios y genera los artefactos:

	```powershell
	mvn clean package
	```

3. Inicia `audit-service` en una terminal (debe mantenerse activo):

	```powershell
	java -jar audit-service/target/audit-service-1.0.0-SNAPSHOT.jar
	```

4. Inicia `order-service` en una segunda terminal:

	```powershell
	java -jar order-service/target/order-service-1.0.0-SNAPSHOT.jar
	```

5. Revisa los endpoints:
   - `http://localhost:8081/swagger-ui.html` para la documentacion de auditoria.
   - `http://localhost:8080/swagger-ui.html` para la documentacion de ordenes.

> Ambos servicios usan el perfil `local` por defecto, con H2 en memoria y secretos resueltos desde `application.yml`.

### Alternativa rapida (Spring Boot Dev Mode)

Si no necesitas los JARs puedes levantar cada microservicio directamente con el plugin de Spring Boot. Ejecuta los siguientes comandos en terminales separadas:

```powershell
cd audit-service
mvn -am compile
mvn spring-boot:run -DskipTests
```

```powershell
cd order-service
mvn -am compile
mvn spring-boot:run -DskipTests
```

> Si prefieres permanecer en la carpeta raíz, agrega el `mainClass` del módulo: `mvn -pl audit-service -am spring-boot:run -DskipTests -Dspring-boot.run.mainClass=com.idm.challenge.audit.AuditServiceApplication` (ajusta el paquete para `order-service`).

El comando `compile` previo garantiza que MapStruct genere los mappers (`OrderEntityMapperImpl`, etc.) antes del arranque. La bandera `-am` (also make) compila cualquier dependencia requerida dentro del mismo reactor.

### Variables de entorno utiles

Para sobreescribir secretos locales sin editar `application.yml`, define estas variables antes de iniciar los servicios (ejemplo en PowerShell):

```powershell
$env:IDM_LOCAL_SECRETS_AUDIT_SERVICE_BASE_URL = "http://localhost:8081"
$env:IDM_LOCAL_SECRETS_AUDIT_SERVICE_API_KEY = "local-audit-key"
```

`order-service` tomara la URL base y la clave API desde estas variables si estan presentes.

### Ejecutar con Docker Compose

```powershell
docker compose up --build
```

Compose construye ambos artefactos y monta las variables necesarias:

- `order-service` expuesto en `http://localhost:8080`
- `audit-service` expuesto en `http://localhost:8081`

## Endpoints principales

| Servicio        | Método | Ruta           | Descripción                                          |
|-----------------|--------|----------------|------------------------------------------------------|
| order-service   | POST   | `/api/orders`  | Crea una orden, persiste y audita (estado confirmado) |
| order-service   | GET    | `/api/orders/{orderId}` | Retorna la orden solicitada junto con sus auditorias |
| audit-service   | POST   | `/api/audit`   | Registra auditoría, requiere cabecera `X-API-Key`     |

Documentación interactiva disponible en `http://localhost:{puerto}/swagger-ui.html`.

### Flujo de creación de orden

1. `order-service` valida la petición (`CreateOrderRequest`).
2. `CreateOrderService` crea la orden en estado `PENDING` y la persiste vía puerto `OrderPersistencePort`.
3. Se invoca `AuditLogPort` (adaptador WebClient) utilizando secretos (`audit-service-base-url`, `audit-service-api-key`).
4. Al completarse la auditoría, la orden se actualiza a `CONFIRMED` y se retorna al cliente.

### Seguridad

- Clave API obtenida desde `SecretProvider` y enviada en `X-API-Key` hacia `audit-service`.
- `audit-service` valida la cabecera antes de procesar la solicitud.

## Datos en memoria

Cada servicio usa su propia base H2 en memoria inicializada con `db/schema.sql` en arranque. No se requieren scripts externos.

## Pruebas

```powershell
mvn test
```

Cobertura:

- Servicios de aplicación (`CreateOrderService`, `RegisterAuditService`).
- Controladores WebFlux con validaciones y manejo de API Key.

## Arquitectura hexagonal resumida

- **Domain**: records `Order`, `AuditRecord`, enums y lógica pura.
- **Application**: puertos de entrada (use cases) y servicios orquestadores.
- **Infrastructure**: adaptadores Web (controladores), persistencia (R2DBC + MapStruct), clientes externos (`WebClient`), proveedores de secretos y configuración.

Este diseño facilita probar cada componente aislado, reemplazar adaptadores (p. ej. migrar de H2 a Postgres R2DBC o de Key Vault a otro proveedor) y mantener contratos claros.

## Próximos pasos sugeridos

- Añadir métricas personalizadas y traces distribuidos (Micrometer/OpenTelemetry).
- Incorporar logs estructurados (logback JSON) y correlación de requests.
- Extender auditoría con colas asíncronas (p. ej. Azure Service Bus) para desacoplar en escenarios de alta carga.
