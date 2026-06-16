# document-service

Microservicio transversal de documentos para Banco BanQuito V2. Sirve tanto para Core como para Switch y administra metadata documental, tipos documentales, versiones, payloads de laboratorio y eventos asociados a evidencias del sistema.

## Responsabilidad

Este servicio no reemplaza las bases transaccionales ni contables. Su función es registrar y consultar evidencias, comprobantes, archivos, reportes y payloads documentales asociados a procesos de negocio.

Casos principales:

- Comprobantes de depósitos, retiros y transferencias.
- Evidencias de cierre EOD y balances de comprobación CSV.
- Archivos de entrada y reportes de novedades de pagos masivos del Switch.
- Evidencias de notificaciones enviadas.
- Evidencias de auditoría de seguridad.

## Stack

- Java 21 LTS
- Spring Boot 4.0.6
- Spring Data MongoDB
- Spring Security JWT
- OpenAPI / Swagger
- Docker
- MongoDB 7

## Paquete base

```text
com.banquito.platform.document
```

## Endpoints principales

```text
GET  /api/v1/documents/types
POST /api/v1/documents
GET  /api/v1/documents/{documentUuid}
GET  /api/v1/documents?context=&type=&businessReferenceUuid=
GET  /api/v1/documents/{documentUuid}/download

POST /api/v1/documents/{documentUuid}/versions
GET  /api/v1/documents/{documentUuid}/versions

POST /api/v1/documents/{documentUuid}/events
GET  /api/v1/documents/{documentUuid}/events

POST /internal/v1/documents  # uso interno protegido
```

## OpenAPI

```text
http://localhost:8086/swagger-ui.html
http://localhost:8086/api-docs
```

## Health

```text
http://localhost:8086/actuator/health
```

## Configuración por variables de entorno

Revisar `.env.example`.

Variables clave:

```text
SERVER_PORT
MONGODB_URI
JWT_ISSUER
JWT_SECRET
DOCUMENT_DEMO_ENABLED
DOCUMENT_GRPC_PORT
DOCUMENT_STORAGE_MODE
```

## Docker local

```bash
mvn clean package
docker build -t banquito/document-service:local .
docker run --rm -p 8086:8086 --env-file .env.example banquito/document-service:local
```

## Notas de arquitectura

- REST/OpenAPI se expone hacia Kong para consumo externo entre sistemas.
- gRPC es el contrato interno activo para comunicación entre microservicios del ecosistema.
- La integración actual usa gRPC interno protegido por metadata y el outbox programado del microservicio productor. RabbitMQ no forma parte del Core en esta fase.
- MongoDB guarda documentos operativos y evidencias, no saldos ni datos maestros bancarios.

## Seguridad y autorización agregada

Los endpoints documentales quedan restringidos a roles internos o clientes técnicos con scope `document.create`. Los clientes finales no deben consultar documentos de otros contextos directamente; el acceso debe pasar por el microservicio dueño del proceso de negocio.



## Registro idempotente de evidencias internas

La comunicación activa desde `core-account-service` usa `DocumentQueryService/RegisterDocument` por gRPC en el puerto `9096`, protegida mediante metadata `x-internal-service-key`.

`POST /internal/v1/documents` se conserva temporalmente como compatibilidad técnica, sin consumidores en Account, hasta completar la prueba de regresión y su posterior retiro controlado.

La combinación `businessContext + documentType + businessReferenceUuid` identifica una evidencia de negocio. Reintentar el mismo evento devuelve el documento ya registrado y evita duplicados en MongoDB.

En esta fase se almacena una representación canónica JSON del comprobante. El PDF visual que genera el frontend puede seguir descargándose sin obligar a persistir cada exportación del usuario.
