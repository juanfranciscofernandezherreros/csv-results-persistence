package com.fernandez.resultspersistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "results")
public class ResultEntity {

    @Id
    @Column(name = "match_id", nullable = false, updatable = false)
    private String matchId;

    @Column(name = "source_event_id", nullable = false)
    private String sourceEventId;

    @Column(name = "event_time", nullable = false)
    private String eventTime;

    @Column(name = "home_team", nullable = false)
    private String homeTeam;

    @Column(name = "away_team", nullable = false)
    private String awayTeam;

    @Column(name = "home_score")
    private Integer homeScore;

    @Column(name = "away_score")
    private Integer awayScore;

    @Column(name = "home_score1")
    private Integer homeScore1;
    @Column(name = "home_score2")
    private Integer homeScore2;
    @Column(name = "home_score3")
    private Integer homeScore3;
    @Column(name = "home_score4")
    private Integer homeScore4;
    @Column(name = "home_score5")
    private Integer homeScore5;

    @Column(name = "away_score1")
    private Integer awayScore1;
    @Column(name = "away_score2")
    private Integer awayScore2;
    @Column(name = "away_score3")
    private Integer awayScore3;
    @Column(name = "away_score4")
    private Integer awayScore4;
    @Column(name = "away_score5")
    private Integer awayScore5;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "competition", nullable = false)
    private String competition;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    @PreUpdate
    void touchUpdatedAt() {
        updatedAt = Instant.now();
    }

    public String getMatchId() { return matchId; }
    public void setMatchId(String matchId) { this.matchId = matchId; }
    public String getSourceEventId() { return sourceEventId; }
    public void setSourceEventId(String sourceEventId) { this.sourceEventId = sourceEventId; }
    public String getEventTime() { return eventTime; }
    public void setEventTime(String eventTime) { this.eventTime = eventTime; }
    public String getHomeTeam() { return homeTeam; }
    public void setHomeTeam(String homeTeam) { this.homeTeam = homeTeam; }
    public String getAwayTeam() { return awayTeam; }
    public void setAwayTeam(String awayTeam) { this.awayTeam = awayTeam; }
    public Integer getHomeScore() { return homeScore; }
    public void setHomeScore(Integer homeScore) { this.homeScore = homeScore; }
    public Integer getAwayScore() { return awayScore; }
    public void setAwayScore(Integer awayScore) { this.awayScore = awayScore; }
    public Integer getHomeScore1() { return homeScore1; }
    public void setHomeScore1(Integer homeScore1) { this.homeScore1 = homeScore1; }
    public Integer getHomeScore2() { return homeScore2; }
    public void setHomeScore2(Integer homeScore2) { this.homeScore2 = homeScore2; }
    public Integer getHomeScore3() { return homeScore3; }
    public void setHomeScore3(Integer homeScore3) { this.homeScore3 = homeScore3; }
    public Integer getHomeScore4() { return homeScore4; }
    public void setHomeScore4(Integer homeScore4) { this.homeScore4 = homeScore4; }
    public Integer getHomeScore5() { return homeScore5; }
    public void setHomeScore5(Integer homeScore5) { this.homeScore5 = homeScore5; }
    public Integer getAwayScore1() { return awayScore1; }
    public void setAwayScore1(Integer awayScore1) { this.awayScore1 = awayScore1; }
    public Integer getAwayScore2() { return awayScore2; }
    public void setAwayScore2(Integer awayScore2) { this.awayScore2 = awayScore2; }
    public Integer getAwayScore3() { return awayScore3; }
    public void setAwayScore3(Integer awayScore3) { this.awayScore3 = awayScore3; }
    public Integer getAwayScore4() { return awayScore4; }
    public void setAwayScore4(Integer awayScore4) { this.awayScore4 = awayScore4; }
    public Integer getAwayScore5() { return awayScore5; }
    public void setAwayScore5(Integer awayScore5) { this.awayScore5 = awayScore5; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getCompetition() { return competition; }
    public void setCompetition(String competition) { this.competition = competition; }
    public Instant getUpdatedAt() { return updatedAt; }
}
