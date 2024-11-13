package com.codewithcled.fullstack_backend_proj1.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a user in the system.
 */
@Entity
public class User {
    /**
     * The unique identifier for the user.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * The username of the user.
     */
    private String username;

    /**
     * The password of the user. This field is write-only.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /**
     * The email of the user.
     */
    private String email;

    /**
     * The role of the user. Default is "ROLE_USER".
     */
    private String role = "ROLE_USER";

    /**
     * The ELO rating of the user.
     */
    private Double elo = 0.0;

    /**
     * The list of tournaments the user is currently participating in.
     */
    @ManyToMany
    @JoinTable(
            name = "user_tournament",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "tournament_id"))
    @JsonBackReference
    private List<Tournament> currentTournament = new ArrayList<>();

    /**
     * Gets the list of current tournaments the user is participating in.
     *
     * @return the list of current tournaments
     */
    public List<Tournament> getCurrentTournaments() {
        return currentTournament;
    }

    /**
     * Adds a tournament to the list of current tournaments.
     *
     * @param newTournament the tournament to add
     */
    public void addCurrentTournament(Tournament newTournament) {
        currentTournament.add(newTournament);
    }

    /**
     * Removes a tournament from the list of current tournaments.
     *
     * @param tournament the tournament to remove
     */
    public void removeCurrentTournament(Tournament tournament) {
        for (Tournament current : currentTournament) {
            if (Objects.equals(current, tournament)) {
                currentTournament.remove(current);
                break;
            }
        }
    }

    /**
     * Sets the list of current tournaments the user is participating in.
     *
     * @param currentTournaments the list of current tournaments
     */
    public void setCurrentTournaments(List<Tournament> currentTournaments) {
        this.currentTournament = currentTournaments;
    }

    /**
     * Gets the ELO rating of the user.
     *
     * @return the ELO rating
     */
    public Double getElo() {
        return elo;
    }

    /**
     * Sets the ELO rating of the user.
     *
     * @param elo the ELO rating
     */
    public void setElo(Double elo) {
        this.elo = elo;
    }

    /**
     * Gets the unique identifier of the user.
     *
     * @return the unique identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the user.
     *
     * @param id the unique identifier
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the username of the user.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     *
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password of the user.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the user.
     *
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the email of the user.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the user.
     *
     * @param email the email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the role of the user.
     *
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the role of the user.
     *
     * @param role the role
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Checks if this user is equal to another object.
     *
     * @param o the object to compare
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(username, user.username);
    }

    /**
     * Returns the hash code of this user.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }
}