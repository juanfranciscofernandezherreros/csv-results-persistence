package com.fernandez.resultspersistence.mapper;

import com.fernandez.results.avro.MatchResultValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResultMapperTest {

    @Test
    void mapsAvroValueToEntity() {
        MatchResultValue value = MatchResultValue.newBuilder()
                .setSourceEventId("event-1")
                .setMatchId("m1")
                .setEventTime("20:00")
                .setHomeTeam("Home")
                .setAwayTeam("Away")
                .setHomeScore(90)
                .setAwayScore(80)
                .setCountry("es")
                .setCompetition("acb")
                .build();

        var entity = new ResultMapper().toEntity(value);

        assertEquals("m1", entity.getMatchId());
        assertEquals("event-1", entity.getSourceEventId());
        assertEquals(90, entity.getHomeScore());
        assertEquals(80, entity.getAwayScore());
        assertEquals("es", entity.getCountry());
        assertEquals("acb", entity.getCompetition());
        assertNull(entity.getHomeScore5());
    }
}
