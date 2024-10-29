package com.codewithcled.fullstack_backend_proj1.DTO;

public class SignUpRequest {
    private String username;
    private String password;
    private String email;
    private String role;
    private Double elo = 0.0;

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

}
