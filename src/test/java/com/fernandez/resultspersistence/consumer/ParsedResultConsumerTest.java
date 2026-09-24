package com.fernandez.resultspersistence.consumer;

import com.fernandez.results.avro.MatchResultKey;
import com.fernandez.results.avro.MatchResultValue;
import com.fernandez.resultspersistence.service.ResultPersistenceService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class ParsedResultConsumerTest {

    @Test
    void persistsConsumedResult() {
        ResultPersistenceService service = mock(ResultPersistenceService.class);
        ParsedResultConsumer consumer = new ParsedResultConsumer(service);

        MatchResultKey key = MatchResultKey.newBuilder()
                .setSourceEventId("event-1")
                .setMatchId("m1")
                .build();
        MatchResultValue value = MatchResultValue.newBuilder()
                .setSourceEventId("event-1")
                .setMatchId("m1")
                .setEventTime("20:00")
                .setHomeTeam("Home")
                .setAwayTeam("Away")
                .setCountry("es")
                .setCompetition("acb")
                .build();

        consumer.listen(new ConsumerRecord<>("results.parsed", 0, 0L, key, value));

        verify(service).persist(value);
    }
}
