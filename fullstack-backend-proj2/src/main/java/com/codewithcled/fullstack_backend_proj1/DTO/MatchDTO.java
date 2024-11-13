package com.codewithcled.fullstack_backend_proj1.DTO;

public class MatchDTO {
    private Long id;
    private Long player1;
    private Double player1StartingElo;
    private Long player2;
    private Double player2StartingElo;
    private boolean isComplete = false;
    private Integer result;
    private Double eloChange1;
    private Double eloChange2;

    //Getters and setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getPlayer1() {
        return player1;
    }
    public void setPlayer1(Long player1) {
        this.player1 = player1;
    }
    public Double getPlayer1StartingElo() {
        return player1StartingElo;
    }
    public void setPlayer1StartingElo(Double player1StartingElo) {
        this.player1StartingElo = player1StartingElo;
    }
    public Long getPlayer2() {
        return player2;
    }
    public void setPlayer2(Long player2) {
        this.player2 = player2;
    }
    public Double getPlayer2StartingElo() {
        return player2StartingElo;
    }
    public void setPlayer2StartingElo(Double player2StartingElo) {
        this.player2StartingElo = player2StartingElo;
    }
    public boolean isComplete() {
        return isComplete;
    }
    public void setComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }
    public Integer getResult() {
        return result;
    }
    public void setResult(Integer result) {
        this.result = result;
    }
    public Double getEloChange1() {
        return eloChange1;
    }
    public void setEloChange1(Double eloChange1) {
        this.eloChange1 = eloChange1;
    }
    public Double getEloChange2() {
        return eloChange2;
    }
    public void setEloChange2(Double eloChange2) {
        this.eloChange2 = eloChange2;
    }
    
}
