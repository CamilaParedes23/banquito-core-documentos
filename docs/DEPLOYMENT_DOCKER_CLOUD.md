# Despliegue Docker / Cloud - document-service

## Objetivo

Preparar `document-service` para ejecutarse tanto en local como en nube usando Docker Compose, variables de entorno y red interna común.

## Servicio

```text
document-service
Puerto HTTP: 8086
Puerto gRPC futuro: 9096
Base documental: MongoDB / banquito_document_db
```

## Variables mínimas

```env
SPRING_PROFILES_ACTIVE=docker
SERVER_PORT=8086
MONGODB_URI=mongodb://banquito_mongo:<password>@mongo-document:27017/banquito_document_db?authSource=admin
JWT_ISSUER=banquito-identity-access
JWT_SECRET=<shared-secret>
DOCUMENT_DEMO_ENABLED=true
```

## Docker Compose esperado

```yaml
document-service:
  image: banquito/document-service:latest
  container_name: document-service
  env_file:
    - ./env/document-service.env
  ports:
    - "8086:8086"
  networks:
    - banquito-net
  depends_on:
    mongo-document:
      condition: service_healthy
```

## Kong

Kong debe enrutar:

```text
/api/v1/documents/** -> document-service:8086
```

El microservicio mantiene `strip_path=false`, porque sus rutas internas ya comienzan en `/api/v1/documents`.

## MongoDB

Ejecutar o montar el script:

```text
docs/database/05_document_mongodb.js
```

El script es idempotente y actualiza tipos documentales mínimos.

## Pendientes de integración

- Consumidor RabbitMQ para `document.requested`.
- Cliente/servidor gRPC real cuando otro microservicio necesite registrar evidencia sin REST.
- Integración con `notification-service` para evidencias de envío.
- Integración con `core-accounting-service` para EOD y balance de comprobación.
