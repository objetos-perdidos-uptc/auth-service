# auth-service

Servicio de autenticación de *Lost Objects*, organizado con **arquitectura hexagonal** (puertos y adaptadores) y manejo de errores con el **patrón Result**.

## Estructura

```
co.darpidev.authservicelostobjects
├── domain                      # Núcleo: sin Spring, JPA ni Lombok
│   ├── model                   # User, Email, Role, UserStatus
│   ├── error                   # UserErrors (catálogo de errores de negocio)
│   └── shared                  # Result, AppError, ErrorType
├── application                 # Casos de uso y contratos
│   ├── port.in                 # RegisterUserUseCase, AuthenticateUserUseCase
│   ├── port.out                # UserRepositoryPort, PasswordHasherPort, TokenGeneratorPort
│   └── service                 # Implementación de los casos de uso
└── infrastructure              # Detalles técnicos (adaptadores)
    ├── adapter.in.web          # AuthController, DTOs, ErrorHttpMapper
    ├── adapter.out.persistence # JPA: entidad, repositorio Spring Data, mapper, adaptador
    ├── adapter.out.security    # BCrypt y JWT
    └── config                  # Composition root (BeanConfiguration) y SecurityConfig
```

Regla de dependencias: `infrastructure → application → domain`. Nunca al revés.

## Errores (patrón Result)

Los casos de uso devuelven `Result<T>`; no lanzan excepciones por reglas de negocio. El tipo de error se traduce a HTTP solo en `ErrorHttpMapper`:

| ErrorType | HTTP |
|---|---|
| VALIDATION | 400 |
| UNAUTHORIZED | 401 |
| NOT_FOUND | 404 |
| CONFLICT | 409 |

La respuesta de error es `{"message": "...", "code": "..."}`.

## Service Discovery

Se registra en Eureka como `AUTH-SERVICE`, con heartbeat cada 10 s e `instance-id` único por IP y puerto, de modo que se puede escalar horizontalmente con varias réplicas.

## Variables de entorno

`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`, `EUREKA_URL`.

## Pruebas

```
./mvnw test -Dtest='RegisterUserServiceTest,AuthenticateUserServiceTest,ResultTest'
```

Los casos de uso se prueban con dobles en memoria, sin base de datos ni contexto de Spring.
(`AuthServiceLostobjectsApplicationTests` levanta el contexto completo y requiere PostgreSQL y `JWT_SECRET`.)
