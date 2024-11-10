package com.codewithcled.fullstack_backend_proj1.model;

import jakarta.persistence.*;
import java.util.List;

/**
 * Represents a round in a tournament.
 */
@Entity
public class Round {

    @GeneratedValue
    @Id
    private Long id;

    /**
     * The round number.
     */
    private Integer roundNum;

    /**
     * The scoreboard for the round.
     */
    @Embedded
    private Scoreboard scoreboard;

    /**
     * The list of matches in the round.
     */
    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Match> matchList;

    /**
     * The tournament to which this round belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    /**
     * Indicates whether the round is completed.
     */
    private Boolean isCompleted = false;

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean iscompleted) {
        this.isCompleted = iscompleted;
    }
    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public List<Match> getMatchList() {
        return matchList;
    }

    public void setMatchList(List<Match> matchList) {
        this.matchList = matchList;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public void setScoreboard(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public Integer getRoundNum() {
        return roundNum;
    }

    public void setRoundNum(Integer roundNum) {
        this.roundNum = roundNum;
    }
}
