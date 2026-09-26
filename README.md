![version](https://img.shields.io/badge/version-1.1.0-blue)
# csv-results-persistence

Microservicio Spring Boot/JDK 21 que consume los resultados parseados por `csv-results-parser` desde Kafka y los persiste en PostgreSQL.

## Flujo

```text
csv-results-parser
  -> Kafka topic: results.parsed
  -> ParsedResultConsumer
  -> ResultPersistenceService
  -> ResultRepository / JPA
  -> PostgreSQL: results
```

El consumidor usa exactamente el contrato Avro `MatchResultKey` / `MatchResultValue` producido por el parser.

## Tabla PostgreSQL

Flyway crea la tabla `results`. La clave primaria es `match_id`, por lo que una reentrega de Kafka o un reprocesado del mismo partido actualiza la fila existente en lugar de crear duplicados.

Campos principales:

- `match_id`
- `source_event_id`
- `event_time`
- `home_team`, `away_team`
- marcador total y parciales `home_score1..5`, `away_score1..5`
- `country`
- `competition`
- `updated_at`

También se crea un índice por `(country, competition)`.

## Configuración

| Variable | Descripción | Default |
|---|---|---|
| `DB_URL` | JDBC URL de PostgreSQL | requerido |
| `DB_USER` | usuario PostgreSQL | requerido |
| `DB_PASS` | contraseña PostgreSQL | requerido |
| `KAFKA_BOOTSTRAP_SERVERS` | brokers Kafka | requerido |
| `KAFKA_SCHEMA_REGISTRY_URL` | Schema Registry | requerido |
| `KAFKA_CONSUMER_GROUP_ID` | grupo consumidor | `csv-results-persistence` |
| `KAFKA_AUTO_OFFSET_RESET` | offset inicial | `earliest` |
| `KAFKA_PARSED_RESULTS_TOPIC` | topic consumido | `results.parsed` |
| `KAFKA_RESULTS_PERSISTENCE_DLT_TOPIC` | topic para mensajes agotados | `results.parsed.DLT` |
| `KAFKA_RETRY_MAX_ATTEMPTS` | intentos totales antes de DLT | `3` |
| `KAFKA_RETRY_BACKOFF_MS` | espera fija entre reintentos | `1000` |

## Desarrollo

Suite rápida sin Docker:

```bash
mvn -B test
```

Suite completa con PostgreSQL real mediante Testcontainers:

```bash
mvn -B verify -Pintegration
```

Construcción:

```bash
mvn clean package -DskipTests
docker build -t csv-results-persistence .
```

## Ejecución

```bash
docker run --rm --name csv-results-persistence \
  -e DB_URL=jdbc:postgresql://postgres:5432/results \
  -e DB_USER=results \
  -e DB_PASS=results \
  -e KAFKA_BOOTSTRAP_SERVERS=kafka:9092 \
  -e KAFKA_SCHEMA_REGISTRY_URL=http://schema-registry:8081 \
  -e KAFKA_PARSED_RESULTS_TOPIC=results.parsed \
  csv-results-persistence
```

Los errores de datos e integridad se consideran permanentes y se envían a DLT sin consumir retries inútiles. Los fallos transitorios de acceso a PostgreSQL se reintentan con backoff configurable; si se agotan los intentos, el registro original se publica en `results.parsed.DLT` conservando los headers de diagnóstico añadidos por Spring Kafka.

## Arquitectura

Ver [docs/diagrams.md](docs/diagrams.md).


Las ramas de pull requests mergeadas se eliminan automáticamente para mantener `main` como rama estable.
