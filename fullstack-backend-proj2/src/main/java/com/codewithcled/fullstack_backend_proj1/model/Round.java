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

    /**
     * Gets whether the round is completed.
     * 
     * @return true if the round is completed, false otherwise.
     */
    public Boolean getIsCompleted() {
        return isCompleted;
    }

    /**
     * Sets whether the round is completed.
     * 
     * @param isCompleted true if the round is completed, false otherwise.
     */
    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    /**
     * Gets the tournament to which this round belongs.
     * 
     * @return the tournament.
     */
    public Tournament getTournament() {
        return tournament;
    }

    /**
     * Sets the tournament to which this round belongs.
     * 
     * @param tournament the tournament.
     */
    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    /**
     * Gets the list of matches in the round.
     * 
     * @return the list of matches.
     */
    public List<Match> getMatchList() {
        return matchList;
    }

    /**
     * Sets the list of matches in the round.
     * 
     * @param matchList the list of matches.
     */
    public void setMatchList(List<Match> matchList) {
        this.matchList = matchList;
    }

    /**
     * Sets the ID of the round.
     * 
     * @param id the ID of the round.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the ID of the round.
     * 
     * @return the ID of the round.
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the scoreboard for the round.
     * 
     * @return the scoreboard.
     */
    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    /**
     * Sets the scoreboard for the round.
     * 
     * @param scoreboard the scoreboard.
     */
    public void setScoreboard(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    /**
     * Gets the round number.
     * 
     * @return the round number.
     */
    public Integer getRoundNum() {
        return roundNum;
    }

    /**
     * Sets the round number.
     * 
     * @param roundNum the round number.
     */
    public void setRoundNum(Integer roundNum) {
        this.roundNum = roundNum;
    }
}