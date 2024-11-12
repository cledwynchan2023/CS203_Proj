package com.codewithcled.fullstack_backend_proj1.model;
import java.util.*;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;

/**
 * Represents a scoreboard that contains a list of scoreboard entries.
 */
@Embeddable
public class Scoreboard {
    @ElementCollection
    private List<ScoreboardEntry> scoreboardEntries;

    /**
     * Gets the list of scoreboard entries.
     * 
     * @return the list of scoreboard entries.
     */
    public List<ScoreboardEntry> getScoreboardEntries() {
        return scoreboardEntries;
    }

    /**
     * Sets the list of scoreboard entries.
     * 
     * @param scoreboardEntries the list of scoreboard entries.
     */
    public void setScoreboardEntries(List<ScoreboardEntry> scoreboardEntries) {
        this.scoreboardEntries = scoreboardEntries;
    }

    /**
     * Sorts the scoreboard entries using the provided comparator.
     * 
     * @param comparator the comparator to use for sorting the scoreboard entries.
     */
    public void sortScoreboard(Comparator<ScoreboardEntry> comparator){
        scoreboardEntries.sort(comparator);
    }

    /**
     * Gets the score of a player by their ID.
     * 
     * @param playerId the ID of the player.
     * @return the score of the player, or null if the player is not found.
     */
    public Double getPlayerScore(long playerId){
        for(ScoreboardEntry entry : scoreboardEntries){
            if(entry.getPlayerId() == playerId){
                return entry.getScore();
            }
        }
        return null;
    }

    /**
     * Updates the score of a player by their ID.
     * 
     * @param playerId the ID of the player.
     * @param newScore the new score to set for the player.
     */
    public void updatePlayerScore(long playerId, double newScore){
        for(ScoreboardEntry entry : scoreboardEntries){
            if(entry.getPlayerId() == playerId){
                entry.setScore(newScore);
                return;
            }
        }
    }
}