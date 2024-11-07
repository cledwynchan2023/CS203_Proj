package com.codewithcled.fullstack_backend_proj1.model;
import java.util.*;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;

@Embeddable
public class Scoreboard {
    @ElementCollection
    private List<ScoreboardEntry> entries;

    public List<ScoreboardEntry> getScoreboardEntries() {
        return entries;
    }

    public void setEntries(List<ScoreboardEntry> entries) {
        this.entries = entries;
    }

    public void sortScoreboard(Comparator<ScoreboardEntry> comparator){
        entries.sort(comparator);
    }

    public Double getPlayerScore(long playerId){
        for(ScoreboardEntry entry : entries){
            if(entry.getPlayerId() == playerId){
                return entry.getScore();
            }
        }
        return null;
    }

    public void updatePlayerScore(long playerId, double newScore){
        for(ScoreboardEntry entry : entries){
            if(entry.getPlayerId() == playerId){
                entry.setScore(newScore);
                return;
            }
        }
    }
}
