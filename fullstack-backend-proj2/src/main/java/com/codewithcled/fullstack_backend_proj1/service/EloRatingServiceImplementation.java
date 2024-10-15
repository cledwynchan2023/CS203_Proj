package com.codewithcled.fullstack_backend_proj1.service;


import java.util.List;
import java.util.Arrays;
import org.springframework.stereotype.Service;

@Service
public class EloRatingServiceImplementation implements EloRatingService {

    @Override
    public boolean isValidElo(int elo){
        if (elo<=0){
            return false;
        }

        return true;
    }

    @Override
    public double WinProbabilityOnElo(int eloA, int eloB) {
        // Takes in two Elo ratings, calculates the probability of Elo B winning over
        // EloA
        return 1.0 / (1 + Math.pow(10, (eloA - eloB) / 400.0));
    }

    @Override
    public double WinValue(int outcome) {
        // Integer Outcome, 1 for EloA win, 0 for EloB win, 0.5 if draw

        if (outcome == -1) {
            return 1;// A wins
        } else if (outcome == 1) {
            return 0;// B wins
        } else if (outcome == 0) {
            return 0.5;// Draw
        }

        return -1;
    }

    // Returns new Elo score of user A
    @Override
    public double EloCalculation(int eloA, int eloB, int outcome) {

        if(!isValidElo(eloA) || !isValidElo(eloB)){
            throw new IllegalArgumentException("Invalid elo values");
        }

        double winValue = WinValue(outcome);

        if (winValue == -1) {
            throw new IllegalArgumentException("Invalid match result only accepts -1 for A wins, 1 for B wins and 0 for draw");
        }

        int k=getKValue(eloA);

        // Probability of player A winning over player B
        double p1Win = WinProbabilityOnElo(eloA, eloB);
        return Math.round(eloA + eloChange(k, winValue, p1Win));
    }

    @Override
    public double eloChange(int k,double winValue, double winProbability){
        return k * (winValue-winProbability);
    }

    @Override
    public int getKValue(int eloScore) {
        if (eloScore >= 2400) {
            return 10;
        } else {
            return 20;
        }
    }

    @Override
    public List<Double> eloRatingForBoth(int elo1,int elo2,int outcome){
        double newEloA=EloCalculation(elo1, elo2, outcome);
        double newEloB=EloCalculation(elo2, elo1, outcome*-1);
        return Arrays.asList(newEloA,newEloB);
    }
}
