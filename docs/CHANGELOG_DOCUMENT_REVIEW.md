# Changelog técnico - document-service

## Cloud-ready review

Cambios aplicados:

- Se confirma naming definitivo `document-service` sin prefijo `core`.
- Se parametriza `application.yml` y `application-docker.yml`.
- Se agrega `Dockerfile` multi-stage.
- Se agrega `.env.example`.
- Se agrega `.gitignore` y `.dockerignore`.
- Se agrega script MongoDB idempotente en `docs/database/05_document_mongodb.js`.
- Se agrega `ownerService` al catálogo de tipos documentales para distinguir Core, Switch, Identity, Accounting y Notification.
- Se mantiene seguridad JWT compatible con `identity-access-service`.
- Se mantiene manejo profesional de errores con `BusinessException`, `@RestControllerAdvice`, `AuthenticationEntryPoint` y `AccessDeniedHandler`.
- Se documenta despliegue Docker/cloud.

## Pendientes posteriores

- Implementar gRPC real según contratos internos.
- Implementar consumidores RabbitMQ para solicitud documental asíncrona.
- Integrar con `core-account-service`, `core-accounting-service`, `notification-service` y Switch.
- Evaluar storage externo real si el proyecto sube de MongoDB payload a S3/GCS/volumen persistente.
