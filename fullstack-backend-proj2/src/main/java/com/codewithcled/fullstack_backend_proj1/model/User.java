package com.codewithcled.fullstack_backend_proj1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.annotation.LastModifiedDate;

@Entity

public class User {
    @Id
    @GeneratedValue
    private Long id;
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String email;
    private String role = "ROLE_USER";
    private Integer elo = 0;

    @ElementCollection
    private List<Long> tournamentsParticipating = new ArrayList();

    public List<Long> getTournamentsParticipating() {
        return tournamentsParticipating;
    }

    public void addParticipatingTournament(Long tournament_id){
        tournamentsParticipating.add(tournament_id);
    }

    public void removeParticipatingTournament(Tournament tournament){
        for (Long current: tournamentsParticipating){
            if (Objects.equals(current, tournament.getId())){
                tournamentsParticipating.remove(current);
                break;
            }
        }
    }

    public void setTournamentsParticipating(List<Long> tournamentsParticipated) {
        this.tournamentsParticipating = tournamentsParticipated;
    }

    public Integer getElo() {
        return elo;
    }

    public void setElo(Integer elo) {
        this.elo = elo;
    }


    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }



   
}
