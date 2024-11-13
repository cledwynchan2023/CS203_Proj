package com.codewithcled.fullstack_backend_proj1.DTO;

public class RankingDTO {
    Integer rank;
    Double elo;

    // Getters and setters
    public Integer getRank() {
        return rank;
    }
    public void setRank(Integer rank) {
        this.rank = rank;
    }
    public Double getElo() {
        return elo;
    }
    public void setElo(Double elo) {
        this.elo = elo;
    }

}
