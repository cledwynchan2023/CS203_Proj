package com.codewithcled.fullstack_backend_proj1.model;

import jakarta.persistence.*;

/**
 * Represents a match in the system.
 */
@Entity
@Table(name = "matches")
public class Match {
    /**
     * The ID of the match.
     */
    @GeneratedValue
    @Id
    private Long id;

    /**
     * The round associated with the match.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id", nullable = false)
    private Round round;

    /**
     * The ID of player 1.
     */
    private Long player1;

    /**
     * The starting Elo rating for player 1.
     */
    private Double player1StartingElo;

    /**
     * The ID of player 2.
     */
    private Long player2;

    /**
     * The starting Elo rating for player 2.
     */
    private Double player2StartingElo;

    /**
     * Indicates whether the match is complete.
     */
    private boolean isComplete = false;

    /**
     * The result of the match. -1 for player 1 win, 0 for draw, 1 for player 2 win.
     */
    private Integer result;

    /**
     * The Elo change for player 1.
     */
    private Double eloChange1;

    /**
     * The Elo change for player 2.
     */
    private Double eloChange2;

    /**
     * Sets the starting Elo rating for player 1.
     * 
     * @param elo the starting Elo rating for player 1.
     */
    public void setPlayer1StartingElo(Double elo) {
        this.player1StartingElo = elo;
    }

    /**
     * Gets the starting Elo rating for player 1.
     * 
     * @return the starting Elo rating for player 1.
     */
    public Double getPlayer1StartingElo() {
        return this.player1StartingElo;
    }

    /**
     * Sets the starting Elo rating for player 2.
     * 
     * @param elo the starting Elo rating for player 2.
     */
    public void setPlayer2StartingElo(Double elo) {
        this.player2StartingElo = elo;
    }

    /**
     * Gets the starting Elo rating for player 2.
     * 
     * @return the starting Elo rating for player 2.
     */
    public Double getPlayer2StartingElo() {
        return this.player2StartingElo;
    }

    /**
     * Sets the ID of the match.
     * 
     * @param id the ID of the match.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the ID of the match.
     * 
     * @return the ID of the match.
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the round associated with the match.
     * 
     * @return the round associated with the match.
     */
    public Round getRound() {
        return round;
    }

    /**
     * Sets the round associated with the match.
     * 
     * @param round the round to associate with the match.
     */
    public void setRound(Round round) {
        this.round = round;
    }

    /**
     * Gets the ID of player 1.
     * 
     * @return the ID of player 1.
     */
    public Long getPlayer1() {
        return player1;
    }

    /**
     * Sets the ID of player 1.
     * 
     * @param player1 the ID of player 1.
     */
    public void setPlayer1(Long player1) {
        this.player1 = player1;
    }

    /**
     * Gets the ID of player 2.
     * 
     * @return the ID of player 2.
     */
    public Long getPlayer2() {
        return player2;
    }

    /**
     * Sets the ID of player 2.
     * 
     * @param player2 the ID of player 2.
     */
    public void setPlayer2(Long player2) {
        this.player2 = player2;
    }

    /**
     * Gets the result of the match.
     * 
     * @return the result of the match.
     */
    public Integer getResult() {
        return result;
    }

    /**
     * Sets the result of the match.
     * 
     * @param result the result of the match.
     */
    public void setResult(Integer result) {
        this.result = result;
    }

    /**
     * Gets the Elo change for player 1.
     * 
     * @return the Elo change for player 1.
     */
    public Double getEloChange1() {
        return eloChange1;
    }

    /**
     * Sets the Elo change for player 1.
     * 
     * @param eloChange1 the Elo change for player 1.
     */
    public void setEloChange1(Double eloChange1) {
        this.eloChange1 = eloChange1;
    }

    /**
     * Gets the Elo change for player 2.
     * 
     * @return the Elo change for player 2.
     */
    public Double getEloChange2() {
        return eloChange2;
    }

    /**
     * Sets the Elo change for player 2.
     * 
     * @param eloChange2 the Elo change for player 2.
     */
    public void setEloChange2(Double eloChange2) {
        this.eloChange2 = eloChange2;
    }

    /**
     * Sets whether the match is complete.
     * 
     * @param bool true if the match is complete, false otherwise.
     */
    public void setIsComplete(boolean bool) {
        this.isComplete = bool;
    }

    /**
     * Gets whether the match is complete.
     * 
     * @return true if the match is complete, false otherwise.
     */
    public boolean getIsComplete() {
        return this.isComplete;
    }
}