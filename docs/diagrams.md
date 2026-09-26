# Diagramas — csv-results-persistence

## Componentes

```mermaid
flowchart LR
    P["csv-results-parser"] --> K["Kafka: results.parsed"]
    K --> C["ParsedResultConsumer"]
    C --> S["ResultPersistenceService"]
    S --> M["ResultMapper"]
    S --> U["ResultUpsertRepository"]
    U --> PG["PostgreSQL: results"]
    R["ResultRepository (lecturas)"] --> PG
```

## Secuencia

```mermaid
sequenceDiagram
    participant K as results.parsed
    participant C as ParsedResultConsumer
    participant S as ResultPersistenceService
    participant U as ResultUpsertRepository
    participant DB as PostgreSQL

    K->>C: MatchResultKey + MatchResultValue
    C->>S: persist(value)
    S->>U: upsert(result)
    U->>DB: INSERT ... ON CONFLICT(match_id) DO UPDATE
    DB-->>U: una fila insertada o actualizada
    U-->>S: ok
    S-->>C: commit
    C-->>K: listener finaliza / offset confirmable
```

La primary key `match_id` es la invariante de negocio que permite resolver redeliveries y reimportaciones en una única operación atómica. `source_event_id` conserva la trazabilidad de la importación que produjo el estado actual.
