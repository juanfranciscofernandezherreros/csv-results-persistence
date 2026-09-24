package com.fernandez.resultspersistence.mapper;

import com.fernandez.results.avro.MatchResultValue;
import com.fernandez.resultspersistence.entity.ResultEntity;
import org.springframework.stereotype.Component;

@Component
public class ResultMapper {

    public ResultEntity toEntity(MatchResultValue value) {
        ResultEntity entity = new ResultEntity();
        entity.setMatchId(value.getMatchId());
        entity.setSourceEventId(value.getSourceEventId());
        entity.setEventTime(value.getEventTime());
        entity.setHomeTeam(value.getHomeTeam());
        entity.setAwayTeam(value.getAwayTeam());
        entity.setHomeScore(value.getHomeScore());
        entity.setAwayScore(value.getAwayScore());
        entity.setHomeScore1(value.getHomeScore1());
        entity.setHomeScore2(value.getHomeScore2());
        entity.setHomeScore3(value.getHomeScore3());
        entity.setHomeScore4(value.getHomeScore4());
        entity.setHomeScore5(value.getHomeScore5());
        entity.setAwayScore1(value.getAwayScore1());
        entity.setAwayScore2(value.getAwayScore2());
        entity.setAwayScore3(value.getAwayScore3());
        entity.setAwayScore4(value.getAwayScore4());
        entity.setAwayScore5(value.getAwayScore5());
        entity.setCountry(value.getCountry());
        entity.setCompetition(value.getCompetition());
        return entity;
    }
}
