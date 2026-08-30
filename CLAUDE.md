# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Spring Boot 3.3 / Java 17 REST API for a multi-tenant bodega (small shop) POS system: products, categories,
suppliers, stock movements, sales, expenses, users and companies, secured with JWT. Uses MySQL + Spring Data JPA,
Lombok, springdoc-openapi (Swagger UI), and jjwt for tokens.

## Commands

- Build: `./mvnw clean install` (Windows: `mvnw.cmd clean install`)
- Compile only: `./mvnw compile`
- Run the app: `./mvnw spring-boot:run` (starts on port `8080`)
- Run all tests: `./mvnw test`
- Run a single test class: `./mvnw test -Dtest=BodegasApplicationTests`
- Run a single test method: `./mvnw test -Dtest=BodegasApplicationTests#contextLoads`
- Package a jar: `./mvnw package`

Requires a local MySQL instance matching `src/main/resources/application.properties`
(`spring.datasource.url`, default DB `pos_bodegas` on port 3306, user/password `root`/`root`).
`spring.jpa.hibernate.ddl-auto=validate` — the schema must already exist and match the entities;
this app does not create/alter tables on startup.

Swagger UI is available at `/swagger-ui.html` when the app is running.

## Architecture

**Layering**: `controller` → `services` (interface) → `services.impl` (implementation) → `repository`
(Spring Data JPA) → `repository.entity`. Controllers depend on service **interfaces**, never on `*ServiceImpl`
classes directly — keep this when adding new features (define the interface in `services`, implement it in
`services.impl`, annotate the impl with `@Service`).

**Multi-tenancy**: almost every table has an `empresaId` (company) column, and nearly every repository/service
method takes `empresaId` as a parameter to scope queries (`findByIdAndEmpresaId`, `findByEmpresaId`, etc.).
When adding a new entity/endpoint, follow this pattern — never fetch by raw `id` alone for tenant-owned data.

**Auth flow**: `AuthController` → `AuthService.login()` validates credentials and calls `JwtUtil.generateToken()`,
embedding `empresaId`, `usuarioId` and `rol` as JWT claims. On every subsequent request, `JwtFilter`
(a `OncePerRequestFilter` registered before `UsernamePasswordAuthenticationFilter` in `SecurityConfig`) validates
the token and stores `empresaId` and `usuarioId` as **request attributes** (`request.setAttribute(...)`), plus
populates `SecurityContextHolder` for role-based checks. Controllers read `empresaId`/`usuarioId` back out of the
request via a small private helper — do not re-derive `usuarioId` by looking up `UsuarioRepository` by principal
name in a controller; it's already on the request attribute.

**Error handling**: exceptions live in `com.pos_bodega.bodegas.exception` and are translated to HTTP responses by
the single `GlobalExceptionHandler` (`@RestControllerAdvice`):
- `ResourceNotFoundException` → 404
- `AuthenticationFailedException` → 401
- `BusinessException` → 400
- any other `RuntimeException` → 400 (fallback; avoid relying on this for new code — throw one of the typed
  exceptions above instead)

**DTOs**: every entity has a `*Request`/`*Response` pair under `controller/dto`; service impls map manually
between entity and DTO (`toResponse(...)` / `mapRequestToEntity(...)` private helpers) — there is no MapStruct or
similar mapping library in use, stay consistent with the existing manual-mapping style.

**Venta (sale) creation is the most business-critical path** (`VentaServiceImpl.crear`, `@Transactional`): it
validates payment type and stock, computes totals, persists the sale + its detail lines, decrements product stock,
and writes a `MovimientoStock` (`TipoMovimiento.VENTA`) audit row per line item — all in one transaction. Any change
to stock/venta logic should preserve this stock-then-movement invariant.

**Package layout note**: entities and repositories both live under `repository` / `repository.entity` in package
declarations that match their directory — keep new repository interfaces in the `repository` package (not
`repository.entity`) and entities in `repository.entity`.

## Pendientes

- **Importación de productos por foto de factura/voucher (visión de Claude) — falta configurar la API key para
  poder probarla.** `FacturaImportServiceImpl` (`services/impl/FacturaImportServiceImpl.java`) llama a la API de
  Claude (`POST https://api.anthropic.com/v1/messages`, modelo por defecto `claude-sonnet-5`) vía
  `POST /api/productos/importar-imagen`. El código está completo y compila, pero sin key configurada tira
  `BusinessException` ("La importación por imagen aún no está configurada..."). Para habilitarla: exportar la
  variable de entorno `ANTHROPIC_API_KEY` (o pegar la key directo en `anthropic.api.key` en
  `application.properties`) y reiniciar el backend. Pendiente: probarla con una foto real de factura una vez
  puesta la key, y ajustar el prompt/parsing en `FacturaImportServiceImpl` si el modelo devuelve algo inesperado.
