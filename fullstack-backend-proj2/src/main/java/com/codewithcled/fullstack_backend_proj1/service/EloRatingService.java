package com.codewithcled.fullstack_backend_proj1.service;

public interface EloRatingService {

    public double WinProbabilityOnElo(int elo1,int elo2);
    
    public double EloCalculation(int elo1,int elo2,int outcome);

    public double WinValue(int outcome);

    public int getKValue(int eloScore);

    public boolean isValidElo(int elo);

    public double eloChange(int k,double winValue, double winProbability);
}
