package com.codewithcled.fullstack_backend_proj1.DTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.codewithcled.fullstack_backend_proj1.model.Round;

public class TournamentStartDTO {
    private Long id;
    private String tournamentName;
    private String date;
    private String status;
    private Integer size;
    private Integer currentSize;
    private Integer noOfRounds;
    private List<UserDTO> participants;
    private Map<Long, Double> scoreboard = new HashMap<>();
    private List<RoundDTO> rounds = new ArrayList<>();

    // Getters and setters
    public Map<Long, Double> getScoreboard() {
        return scoreboard;
    }

    public void setScoreboard(Map<Long, Double> scoreboard) {
        this.scoreboard = scoreboard;
    }

    public List<RoundDTO> getRounds() {
        return rounds;
    }

    public void setRounds(List<RoundDTO> rounds) {
        this.rounds = rounds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTournamentName() {
        return tournamentName;
    }

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(Integer currentSize) {
        this.currentSize = currentSize;
    }

    public Integer getNoOfRounds() {
        return noOfRounds;
    }

    public void setNoOfRounds(Integer noOfRounds) {
        this.noOfRounds = noOfRounds;
    }

    public List<UserDTO> getParticipants() {
        return participants;
    }

    public void setParticipants(List<UserDTO> participants) {
        this.participants = participants;
    }
}