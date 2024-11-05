package com.codewithcled.fullstack_backend_proj1.model;

import jakarta.persistence.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

@Entity
public class Round {

    @GeneratedValue
    @Id
    private Long id;

    private Integer roundNum;

    @ElementCollection
    private Map<Long, Double> scoreboard;

    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Match> matchList;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

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

    public Map<Long, Double> getScoreboard() {
        return scoreboard;
    }

    public void setScoreboard(Map<Long, Double> scoreboard) {
        this.scoreboard = scoreboard;
    }

    public Integer getRoundNum() {
        return roundNum;
    }

    public void setRoundNum(Integer roundNum) {
        this.roundNum = roundNum;
    }
}
