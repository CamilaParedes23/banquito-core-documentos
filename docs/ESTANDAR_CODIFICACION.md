# Estándar aplicado

- Paquete base: `com.banquito.platform.document`.
- Spring Boot 4.0.6, Java 21, Maven.
- MongoDB como base documental.
- Lombok solo `@Getter` y `@Setter` en documentos de dominio.
- No usar `@Data`.
- Enums específicos con sufijo `Enum`.
- `BusinessException` + `@RestControllerAdvice`.
- `AuthenticationEntryPoint` y `AccessDeniedHandler` para 401/403.
- JWT validado con el mismo secreto de `identity-access-service`.
- OpenAPI en `/api-docs` y Swagger en `/swagger-ui.html`.
