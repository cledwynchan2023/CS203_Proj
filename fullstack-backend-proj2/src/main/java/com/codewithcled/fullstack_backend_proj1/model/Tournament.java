package com.codewithcled.fullstack_backend_proj1.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.*;

/**
 * Represents a tournament.
 */
@Entity
public class Tournament {

    /**
     * The ID of the tournament.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * The name of the tournament.
     */
    private String tournament_name;

    /**
     * The list of participants in the tournament.
     */
    @ManyToMany(mappedBy = "currentTournament")
    @JsonManagedReference 
    private List<User> participants = new ArrayList<>();

    /**
     * The date of the tournament.
     */
    private String date;

    /**
     * The status of the tournament (e.g., "active", "ongoing", "completed").
     */
    private String status;

    /**
     * The maximum size of the tournament.
     */
    private Integer size;

    /**
     * The current number of participants in the tournament.
     */
    private Integer currentSize = participants.size();

    /**
     * The number of rounds in the tournament.
     */
    private Integer noOfRounds = 0;

    /**
     * The current round of the tournament.
     */
    private Integer currentRound = 1;

    /**
     * The list of rounds in the tournament.
     */
    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Round> rounds = new ArrayList<>();

    /**
     * Gets the current round of the tournament.
     * 
     * @return the current round of the tournament.
     */
    public Integer getCurrentRound() {
        return currentRound;
    }

    /**
     * Sets the current round of the tournament.
     * 
     * @param currentRound the current round of the tournament.
     */
    public void setCurrentRound(Integer currentRound) {
        this.currentRound = currentRound;
    }

    /**
     * Gets the list of rounds in the tournament.
     * 
     * @return the list of rounds in the tournament.
     */
    public List<Round> getRounds() {
        return rounds;
    }

    /**
     * Sets the list of rounds in the tournament.
     * 
     * @param rounds the list of rounds in the tournament.
     */
    public void setRounds(List<Round> rounds) {
        this.rounds = rounds;
    }

    /**
     * Gets the number of rounds in the tournament.
     * 
     * @return the number of rounds in the tournament.
     */
    public Integer getNoOfRounds() {
        return noOfRounds;
    }

    /**
     * Sets the number of rounds in the tournament.
     * 
     * @param noOfRounds the number of rounds in the tournament.
     */
    public void setNoOfRounds(Integer noOfRounds) {
        this.noOfRounds = noOfRounds;
    }

    /**
     * Gets the current number of participants in the tournament.
     * 
     * @return the current number of participants in the tournament.
     */
    public Integer getCurrentSize() {
        return currentSize;
    }

    /**
     * Sets the current number of participants in the tournament.
     * 
     * @param currentSize the current number of participants in the tournament.
     */
    public void setCurrentSize(Integer currentSize) {
        this.currentSize = currentSize;
    }

    /**
     * Gets the maximum size of the tournament.
     * 
     * @return the maximum size of the tournament.
     */
    public Integer getSize() {
        return size;
    }

    /**
     * Sets the maximum size of the tournament.
     * 
     * @param size the maximum size of the tournament.
     */
    public void setSize(Integer size) {
        this.size = size;
    }

    /**
     * Gets the status of the tournament.
     * 
     * @return the status of the tournament.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the tournament.
     * 
     * @param status the status of the tournament.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the date of the tournament.
     * 
     * @return the date of the tournament.
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the date of the tournament.
     * 
     * @param date the date of the tournament.
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Adds a participant to the tournament.
     * 
     * @param user the participant to add.
     */
    public void addParticipant(User user) {
        participants.add(user);
    }

    /**
     * Removes a participant from the tournament.
     * 
     * @param user the participant to remove.
     */
    public void removeParticipant(User user) {
        for (User current : participants) {
            if (Objects.equals(current, user)) {
                participants.remove(current);
                System.out.println(user.getId() + " deleted!");
                break;
            }
        }
    }

    /**
     * Gets the ID of the tournament.
     * 
     * @return the ID of the tournament.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the tournament.
     * 
     * @param id the ID of the tournament.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the name of the tournament.
     * 
     * @return the name of the tournament.
     */
    public String getTournament_name() {
        return tournament_name;
    }

    /**
     * Sets the name of the tournament.
     * 
     * @param tournament_name the name of the tournament.
     */
    public void setTournament_name(String tournament_name) {
        this.tournament_name = tournament_name;
    }

    /**
     * Gets the list of participants in the tournament.
     * 
     * @return the list of participants in the tournament.
     */
    public List<User> getParticipants() {
        return participants;
    }

    /**
     * Sets the list of participants in the tournament.
     * 
     * @param participants the list of participants in the tournament.
     */
    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    /**
     * Gets the scoreboard of the tournament.
     * 
     * @return the scoreboard of the tournament.
     */
    public Scoreboard getScoreboard() {
        return getRounds().get(getRounds().size() - 1).getScoreboard();
    }

    /**
     * Checks if this tournament is equal to another object.
     * 
     * @param o the object to compare to.
     * @return true if the tournaments are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tournament that = (Tournament) o;
        return Objects.equals(id, that.id);
    }

    /**
     * Gets the hash code of the tournament.
     * 
     * @return the hash code of the tournament.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}