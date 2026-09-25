# Changelog

## 1.0.5

- [patch] Estandariza la automatización del repositorio con el flujo autónomo de csv-results-parser.

## 1.0.4 - 2026-09-25

- [patch] Añade eliminación automática de la rama origen después de mergear una Pull Request en `main`.
- [patch] Mantiene `main` como rama estable y no elimina ninguna otra rama.

## 1.0.1 - 2026-09-24

- Añade limpieza automática de ramas de pull requests mergeadas y elimina la rama histórica residual.
- Actualiza la versión de `1.0.0` a `1.0.1`.

## 1.0.0 - 2026-09-24

- Crea el microservicio `csv-results-persistence`.
- Consume mensajes Avro `MatchResultKey` / `MatchResultValue` desde `results.parsed`.
- Persiste los resultados con Spring Data JPA en PostgreSQL.
- Crea la tabla `results` mediante Flyway y usa `match_id` como clave primaria para soportar reentregas idempotentes.
- Añade tests unitarios y una prueba de integración PostgreSQL con Testcontainers.
- Añade CI con JDK 21 para `mvn -B verify -Pintegration`.
