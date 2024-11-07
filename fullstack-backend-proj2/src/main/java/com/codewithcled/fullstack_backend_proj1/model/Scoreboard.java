package com.codewithcled.fullstack_backend_proj1.model;
import java.util.*;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;

@Embeddable
public class Scoreboard {
    @ElementCollection
    private List<ScoreboardEntry> scoreboardEntries;

    public List<ScoreboardEntry> getScoreboardEntries() {
        return scoreboardEntries;
    }

    public void setScoreboardEntries(List<ScoreboardEntry> scoreboardEntries) {
        this.scoreboardEntries = scoreboardEntries;
    }

    public void sortScoreboard(Comparator<ScoreboardEntry> comparator){
        scoreboardEntries.sort(comparator);
    }

    public Double getPlayerScore(long playerId){
        for(ScoreboardEntry entry : scoreboardEntries){
            if(entry.getPlayerId() == playerId){
                return entry.getScore();
            }
        }
        return null;
    }

    public void updatePlayerScore(long playerId, double newScore){
        for(ScoreboardEntry entry : scoreboardEntries){
            if(entry.getPlayerId() == playerId){
                entry.setScore(newScore);
                return;
            }
        }
    }
}
