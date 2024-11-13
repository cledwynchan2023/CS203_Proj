package com.codewithcled.fullstack_backend_proj1.DTO;

import java.util.List;

public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String role;
    private Double elo;
    private List<TournamentDTO> currentTournament;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
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

    public Double getElo() {
        return elo;
    }

    public void setElo(Double elo) {
        this.elo = elo;
    }
    public List<TournamentDTO> getCurrentTournaments() {
        return currentTournament;
    }
    public void setCurrentTournaments(List<TournamentDTO> currentTournaments) {
        this.currentTournament = currentTournaments;
    }
}