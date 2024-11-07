package com.codewithcled.fullstack_backend_proj1.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class ScoreboardEntry {
    private Long playerId;
    private Double score;

    public ScoreboardEntry(Long playerId, Double score) {
        this.playerId = playerId;
        this.score = score;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
