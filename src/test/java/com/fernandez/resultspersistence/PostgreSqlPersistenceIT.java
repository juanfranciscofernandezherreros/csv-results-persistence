package com.fernandez.resultspersistence;

import com.fernandez.results.avro.MatchResultValue;
import com.fernandez.resultspersistence.repository.ResultRepository;
import com.fernandez.resultspersistence.service.ResultPersistenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    @Test
    void redeliveryOfSameEventKeepsSingleRow() {
        MatchResultValue event = value("event-1", 90);

        service.persist(event);
        service.persist(event);

        assertEquals(1, repository.count());
        var saved = repository.findById("m1").orElseThrow();
        assertEquals("event-1", saved.getSourceEventId());
        assertEquals(90, saved.getHomeScore());
    }

    @Test
    void reimportUpdatesCurrentStateAndTracksLatestSourceEvent() {
        service.persist(value("event-1", 90));
        service.persist(value("event-2", 91));

        assertEquals(1, repository.count());
        var saved = repository.findById("m1").orElseThrow();
        assertEquals("event-2", saved.getSourceEventId());
        assertEquals(91, saved.getHomeScore());
    }

    @Test
    void concurrentRedeliveryKeepsSingleDeterministicState() throws Exception {
        MatchResultValue event = value("event-concurrent", 92);
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            Future<?> first = executor.submit(() -> persistAfter(start, event));
            Future<?> second = executor.submit(() -> persistAfter(start, event));

            start.countDown();
            first.get();
            second.get();

            assertEquals(1, repository.count());
            var saved = repository.findById("m1").orElseThrow();
            assertEquals("event-concurrent", saved.getSourceEventId());
            assertEquals(92, saved.getHomeScore());
        } finally {
            executor.shutdownNow();
        }
    }

    private void persistAfter(CountDownLatch start, MatchResultValue event) {
        try {
            start.await();
            service.persist(event);
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(interrupted);
        }
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
