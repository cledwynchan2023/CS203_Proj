package com.codewithcled.fullstack_backend_proj1.DTO;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.codewithcled.fullstack_backend_proj1.model.Match;
import com.codewithcled.fullstack_backend_proj1.model.Round;
import com.codewithcled.fullstack_backend_proj1.model.Tournament;


public class RoundDTO {
    private Long id;
    private Integer roundNum;
    private Map<Long, Double> scoreboard;
    private List<MatchDTO> matchList;
    
    // Getters and setters
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
