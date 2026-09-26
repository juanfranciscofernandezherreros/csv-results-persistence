![version](https://img.shields.io/badge/version-1.2.0-blue)
# csv-results-persistence

Microservicio Spring Boot/JDK 21 que consume los resultados parseados por `csv-results-parser` desde Kafka y los persiste en PostgreSQL.

## Flujo

```text
csv-results-parser
  -> Kafka topic: results.parsed
  -> ParsedResultConsumer
  -> ResultPersistenceService
  -> ResultUpsertRepository
  -> PostgreSQL: results
```

El consumidor usa exactamente el contrato Avro `MatchResultKey` / `MatchResultValue` producido por el parser.

## Política de idempotencia

La identidad de negocio de RESULTS es `match_id`. PostgreSQL la protege con la primary key de `results`.

La escritura usa una única operación atómica `INSERT ... ON CONFLICT (match_id) DO UPDATE`:

- un redelivery del mismo evento mantiene una única fila;
- una reimportación del mismo partido actualiza el estado materializado actual;
- `source_event_id` conserva la trazabilidad de la importación que produjo la versión vigente;
- no existe una ventana `read-then-write` entre comprobar existencia y guardar;
- redeliveries concurrentes del mismo evento convergen en el mismo estado.

La política de RESULTS es, por tanto, **current-state upsert**: `match_id` identifica el agregado y la última escritura confirmada para ese partido representa su estado actual. El servicio no mantiene histórico de versiones en esta tabla.

## Tabla PostgreSQL

Flyway crea la tabla `results`. La clave primaria es `match_id`, que respalda directamente la política de idempotencia y el `ON CONFLICT`.

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

Suite completa con PostgreSQL real mediante Testcontainers, incluyendo redelivery, reimportación y concurrencia:

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

## Recuperación de deserialización Kafka

Los deserializadores Avro están envueltos con `ErrorHandlingDeserializer`, por lo que un payload corrupto o incompatible entra en el flujo normal de recuperación. La DLT `results.parsed.DLT` acepta tanto objetos Avro como `byte[]` originales, conserva los headers de diagnóstico, deja que Kafka seleccione una partición válida y hace visible cualquier fallo de publicación en la propia DLT.
