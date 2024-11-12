package com.codewithcled.fullstack_backend_proj1.model;

import jakarta.persistence.Embeddable;

/**
 * Represents an entry in the scoreboard.
 */
@Embeddable
public class ScoreboardEntry {
    /**
     * The ID of the player.
     */
    private Long playerId;

    /**
     * The score of the player.
     */
    private Double score;

    /**
     * No-argument constructor.
     */
    public ScoreboardEntry() {
    }

    /**
     * Constructs a ScoreboardEntry with the specified player ID and score.
     * 
     * @param playerId the ID of the player.
     * @param score the score of the player.
     */
    public ScoreboardEntry(Long playerId, Double score) {
        this.playerId = playerId;
        this.score = score;
    }

    /**
     * Gets the ID of the player.
     * 
     * @return the ID of the player.
     */
    public Long getPlayerId() {
        return playerId;
    }

    /**
     * Sets the ID of the player.
     * 
     * @param playerId the ID of the player.
     */
    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    /**
     * Gets the score of the player.
     * 
     * @return the score of the player.
     */
    public Double getScore() {
        return score;
    }

    /**
     * Sets the score of the player.
     * 
     * @param score the score of the player.
     */
    public void setScore(Double score) {
        this.score = score;
    }
}