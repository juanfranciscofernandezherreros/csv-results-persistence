# Repository Rules

## Development contract

1. Use a dedicated branch for every non-merge change.
2. Keep one logical change per commit.
3. Update the Maven version, README and CHANGELOG together.
4. Add or update JUnit tests for functional/configuration changes.
5. Use JDK 21.
6. Run `mvn -B test` for the Docker-free suite.
7. For persistence/migration changes also run `mvn -B verify -Pintegration`.
8. Open a Pull Request against `main`; do not merge until `documentation-policy` and `test` are green.

## Persistence contract

PostgreSQL schema changes must be made with Flyway migrations. Hibernate uses `ddl-auto=validate`; it must not create or mutate production schema implicitly.

## Kafka contract

The service consumes the Avro contract emitted by `csv-results-parser`. Keep the local Avro schemas synchronized with the producer contract and cover changes with tests.
