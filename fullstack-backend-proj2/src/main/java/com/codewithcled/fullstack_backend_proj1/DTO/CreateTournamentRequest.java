package com.codewithcled.fullstack_backend_proj1.DTO;

public class CreateTournamentRequest {

    private String tournament_name;
    private String date;
    private String status;
    private Integer size ;
    private Integer noOfRounds = 0;

    public String getTournament_name() {
        return tournament_name;
    }
    public void setTournament_name(String tournament_name) {
        this.tournament_name = tournament_name;
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
    public Integer getNoOfRounds() {
        return noOfRounds;
    }
    public void setNoOfRounds(Integer noOfRounds) {
        this.noOfRounds = noOfRounds;
    }
    
}