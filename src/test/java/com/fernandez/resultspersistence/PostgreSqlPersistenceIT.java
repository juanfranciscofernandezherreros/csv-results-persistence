package com.fernandez.resultspersistence;

import com.fernandez.results.avro.MatchResultValue;
import com.fernandez.resultspersistence.repository.ResultRepository;
import com.fernandez.resultspersistence.service.ResultPersistenceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest(properties = {
        "spring.kafka.listener.auto-startup=false",
        "spring.kafka.bootstrap-servers=localhost:9092",
        "spring.kafka.properties.schema.registry.url=mock://result-persistence-it"
})
class PostgreSqlPersistenceIT {

    @Container
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("results")
                    .withUsername("results")
                    .withPassword("results");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    ResultPersistenceService service;

    @Autowired
    ResultRepository repository;

    @Test
    void flywayCreatesResultsTableAndServicePersistsIdempotently() {
        MatchResultValue first = value("event-1", 90);
        MatchResultValue replay = value("event-2", 91);

        service.persist(first);
        service.persist(replay);

        assertEquals(1, repository.count());
        var saved = repository.findById("m1");
        assertTrue(saved.isPresent());
        assertEquals("event-2", saved.orElseThrow().getSourceEventId());
        assertEquals(91, saved.orElseThrow().getHomeScore());
    }

    private MatchResultValue value(String eventId, int homeScore) {
        return MatchResultValue.newBuilder()
                .setSourceEventId(eventId)
                .setMatchId("m1")
                .setEventTime("20:00")
                .setHomeTeam("Home")
                .setAwayTeam("Away")
                .setHomeScore(homeScore)
                .setAwayScore(80)
                .setCountry("es")
                .setCompetition("acb")
                .build();
    }
}
