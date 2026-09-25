![version](https://img.shields.io/badge/version-1.0.5-blue)
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

El offset Kafka solo se confirma normalmente después de que el listener termine. Si la persistencia falla, el listener lanza la excepción y el mensaje puede ser reintentado; la clave primaria `match_id` mantiene la operación idempotente frente a reentregas.

## Arquitectura

Ver [docs/diagrams.md](docs/diagrams.md).


Las ramas de pull requests mergeadas se eliminan automáticamente para mantener `main` como rama estable.
