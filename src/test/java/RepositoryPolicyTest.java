import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RepositoryPolicyTest {

    @Test
    void repositoryDocumentsAndConfiguresPersistenceContract() throws Exception {
        String pom = Files.readString(Path.of("pom.xml"));
        String readme = Files.readString(Path.of("README.md"));
        String config = Files.readString(Path.of("src/main/resources/application.yml"));
        String migration = Files.readString(Path.of("src/main/resources/db/migration/V1__create_results.sql"));

        assertTrue(pom.contains("<java.version>21</java.version>"));
        assertTrue(pom.contains("spring-boot-starter-data-jpa"));
        assertTrue(pom.contains("<artifactId>postgresql</artifactId>"));
        assertTrue(pom.contains("kafka-avro-serializer"));
        assertTrue(config.contains("${KAFKA_PARSED_RESULTS_TOPIC:results.parsed}"));
        assertTrue(migration.contains("CREATE TABLE results"));
        assertTrue(readme.contains("Current version: **1.0.2**"));
        assertTrue(readme.contains("results.parsed"));
    }
}
