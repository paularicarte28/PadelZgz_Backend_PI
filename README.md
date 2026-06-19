# PadelZGZ API — Backend

REST API para la plataforma de reserva de pistas de pádel en Zaragoza.

**🔗 Frontend:** [paularicarte28/padelzgz-frontend](https://github.com/paularicarte28/padelzgz-frontend)

---

## Stack tecnológico

| Capa | Tecnología |
|------|-----------|
| Framework | Spring Boot 3.2.0 (Java 17) |
| Seguridad | Spring Security + JWT (JJWT 0.11.5) |
| Persistencia | Spring Data JPA + Hibernate |
| Base de datos | MariaDB 10.x |
| Documentación | SpringDoc OpenAPI 3 (Swagger UI) |
| Tests | JUnit 5 + Mockito + Postman/Newman (137 aserciones) |
| CI/CD | GitHub Actions |
| Contenedores | Docker + Docker Compose |

---

## Entidades

- **Club** → Pistas, Torneos
- **Pista** → Reservas, Valoraciones
- **Usuario** → Reservas, Inscripciones, Valoraciones
- **Torneo** → Inscripciones

---

## Endpoints principales

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/auth/register` | Registro de usuario |
| POST | `/api/auth/login` | Login → JWT |
| GET | `/api/auth/me` | Verificar sesión |
| GET | `/api/clubs` | Listar clubs |
| GET | `/api/pistas` | Listar pistas |
| POST | `/api/reservas` | Crear reserva |
| GET | `/api/reservas/mias` | Mis reservas |

Documentación completa en `/swagger-ui.html` una vez arrancado.

---

## Cómo ejecutar

### Con Docker Compose (recomendado)

```bash
docker-compose up --build
```

La API queda disponible en `http://localhost:8080`

### En local

**Requisitos:** Java 17, Maven, MariaDB corriendo en puerto 3307

```bash
# Crear base de datos
mysql -u root -p -e "CREATE DATABASE padelzgz; CREATE USER 'padeluser'@'localhost' IDENTIFIED BY 'padelpass'; GRANT ALL ON padelzgz.* TO 'padeluser'@'localhost';"

# Arrancar
mvn spring-boot:run
```

---

## Tests

```bash
# Tests unitarios
mvn test

# Tests Postman (requiere Newman y la API corriendo)
npx newman run postman/PadelZGZ_API_RUNNER.postman_collection.json \
  --environment postman/PadelZGZ_CI.postman_environment.json
```

El pipeline de CI/CD en GitHub Actions ejecuta los 137 tests automáticamente en cada push a `main`.

---

## Variables de entorno

| Variable | Descripción | Default |
|----------|-------------|---------|
| `JWT_SECRET` | Clave secreta JWT | valor por defecto en dev |
| `JWT_EXPIRATION` | Expiración token (ms) | `86400000` (24h) |
| `SPRING_PROFILES_ACTIVE` | Perfil activo | `dev` |

---

## Autora

Paula Ricarte — DAM 2024-2025 · Centro San Valero, Zaragoza
