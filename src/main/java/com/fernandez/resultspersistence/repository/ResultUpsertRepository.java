package com.fernandez.resultspersistence.repository;

import com.fernandez.resultspersistence.entity.ResultEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ResultUpsertRepository {

    private static final String UPSERT_SQL = """
            INSERT INTO results (
                match_id, source_event_id, event_time, home_team, away_team,
                home_score, away_score,
                home_score1, home_score2, home_score3, home_score4, home_score5,
                away_score1, away_score2, away_score3, away_score4, away_score5,
                country, competition, updated_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
            ON CONFLICT (match_id) DO UPDATE SET
                source_event_id = EXCLUDED.source_event_id,
                event_time = EXCLUDED.event_time,
                home_team = EXCLUDED.home_team,
                away_team = EXCLUDED.away_team,
                home_score = EXCLUDED.home_score,
                away_score = EXCLUDED.away_score,
                home_score1 = EXCLUDED.home_score1,
                home_score2 = EXCLUDED.home_score2,
                home_score3 = EXCLUDED.home_score3,
                home_score4 = EXCLUDED.home_score4,
                home_score5 = EXCLUDED.home_score5,
                away_score1 = EXCLUDED.away_score1,
                away_score2 = EXCLUDED.away_score2,
                away_score3 = EXCLUDED.away_score3,
                away_score4 = EXCLUDED.away_score4,
                away_score5 = EXCLUDED.away_score5,
                country = EXCLUDED.country,
                competition = EXCLUDED.competition,
                updated_at = CURRENT_TIMESTAMP
            """;

    private final JdbcTemplate jdbcTemplate;

    public ResultUpsertRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void upsert(ResultEntity result) {
        jdbcTemplate.update(
                UPSERT_SQL,
                result.getMatchId(),
                result.getSourceEventId(),
                result.getEventTime(),
                result.getHomeTeam(),
                result.getAwayTeam(),
                result.getHomeScore(),
                result.getAwayScore(),
                result.getHomeScore1(),
                result.getHomeScore2(),
                result.getHomeScore3(),
                result.getHomeScore4(),
                result.getHomeScore5(),
                result.getAwayScore1(),
                result.getAwayScore2(),
                result.getAwayScore3(),
                result.getAwayScore4(),
                result.getAwayScore5(),
                result.getCountry(),
                result.getCompetition()
        );
    }
}
