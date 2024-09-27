package com.codewithcled.fullstack_backend_proj1.DTO;


import java.util.*;
import com.codewithcled.fullstack_backend_proj1.model.Round;


public class TournamentDTO {


    private Long id;
    private String tournament_name;
    private List<Long> participants;
    private Map<Long, Integer> scoreboard;
    private String date;
    private String status;
    private Integer size ;
    private Integer currentSize;
    private Integer noOfRounds;
    private List<Round> rounds;

    public TournamentDTO(Long id, String tournament_name, List<Long> participants, Map<Long, Integer> scoreboard, String date, String status, Integer size, Integer currentSize, Integer noOfRounds, List<Round> rounds) {
        this.id = id;
        this.tournament_name = tournament_name;
        this.participants = participants;
        this.scoreboard = scoreboard;
        this.date = date;
        this.status = status;
        this.size = size;
        this.currentSize = currentSize;
        this.noOfRounds = noOfRounds;
        this.rounds = rounds;
    }

    public List<Round> getRounds() {
        return rounds;
    }

    public Integer getNoOfRounds() {
        return noOfRounds;
    }

    public Integer getCurrentSize(){
        return currentSize;
    }

    public Integer getSize() {
        return size;
    }

    public String getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }

    public Long getId() {
        return id;
    }


    public String getTournament_name() {
        return tournament_name;
    }

    public List<Long> getParticipants() {
        return participants;
    }


    public Map<Long, Integer> getScoreboard() {
        return scoreboard;
    }

}

