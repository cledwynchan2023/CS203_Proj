package com.codewithcled.fullstack_backend_proj1.DTO;

import java.util.List;
import java.util.Map;

public class RoundDTO {
    private Long id;
    private Integer roundNum;
    private Map<Long, Double> scoreboard;
    private List<MatchDTO> matchList;
    private Boolean isCompleted = false;

    // Getters and setters
    public Boolean getIsCompleted() {
        return isCompleted;
    }
    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Integer getRoundNum() {
        return roundNum;
    }
    public void setRoundNum(Integer roundNum) {
        this.roundNum = roundNum;
    }
    public Map<Long, Double> getScoreboard() {
        return scoreboard;
    }
    public void setScoreboard(Map<Long, Double> scoreboard) {
        this.scoreboard = scoreboard;
    }
    public List<MatchDTO> getMatchList() {
        return matchList;
    }
    public void setMatchList(List<MatchDTO> matchList) {
        this.matchList = matchList;
    }


}
