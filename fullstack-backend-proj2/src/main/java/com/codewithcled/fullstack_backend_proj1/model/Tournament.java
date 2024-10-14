package com.codewithcled.fullstack_backend_proj1.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.*;


@Entity
public class Tournament {

    @Id
    @GeneratedValue
    private Long id;
    private String tournament_name;
    @ManyToMany(mappedBy = "currentTournament")
    @JsonManagedReference 
    private List<User> participants = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "tournament_scoreboard", joinColumns = @JoinColumn(name = "tournament_id"))
    @MapKeyColumn(name = "user_id")
    @Column(name = "score")
    private Map<Long, Double> scoreboard;
    private String date;
    private String status; // the statuses are "active", "ongoing", "completed"

    private Integer size ;
    private Integer currentSize = participants.size();

    private Integer noOfRounds = 0;

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Round> rounds = new ArrayList<>();

    public List<Round> getRounds() {
        return rounds;
    }

    public void setRounds(List<Round> rounds) {
        this.rounds = rounds;
    }

    public Integer getNoOfRounds() {
        return noOfRounds;
    }

    public void setNoOfRounds(Integer noOfRounds) {
        this.noOfRounds = noOfRounds;
    }

    public Integer getCurrentSize(){
        return currentSize;
    }

    public void setCurrentSize(Integer currentSize){
        this.currentSize = currentSize;
    }
    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void addParticipant(User user){
        participants.add(user);
    }

    public void removeParticipant(User user){
        for (User current: participants){

            if (Objects.equals(current, user)){
                participants.remove(current);
                System.out.println(user.getId() + " deleted!");
                break;
            }
        }
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTournament_name() {
        return tournament_name;
    }

    public void setTournament_name(String tournament_name) {
        this.tournament_name = tournament_name;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }


    public Map<Long, Double> getScoreboard() {
        return scoreboard;
    }

    public void setScoreboard(Map<Long, Double> scoreboard) {
        this.scoreboard = scoreboard;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tournament that = (Tournament) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
