package com.codewithcled.fullstack_backend_proj1.service;

import java.util.List;

public interface EloRatingService {

    /**
     * Calculates the win probability based on the Elo ratings of two players.
     *
     * @param elo1 the Elo rating of the first player
     * @param elo2 the Elo rating of the second player
     * @return the win probability of the first player
     */
    public double WinProbabilityOnElo(int elo1, int elo2);
    
    /**
     * Calculates the new Elo rating for a player based on the outcome of a match.
     *
     * @param elo1 the Elo rating of the first player
     * @param elo2 the Elo rating of the second player
     * @param outcome the outcome of the match (0 for draw, 1 for player 2 wins, 2 for player 1 wins)
     * @return the new Elo rating for the player
     */
    public double EloCalculation(int elo1, int elo2, int outcome);

    /**
     * Determines the win value based on the outcome of a match.
     *
     * @param outcome the outcome of the match (0 for draw, 1 for player 2 wins, 2 for player 1 wins)
     * @return the win value
     */
    public double WinValue(int outcome);

    /**
     * Retrieves the K-value used in Elo rating calculations based on the player's Elo score.
     *
     * @param eloScore the Elo score of the player
     * @return the K-value
     */
    public int getKValue(int eloScore);

    /**
     * Validates if the given Elo rating is within a valid range.
     *
     * @param elo the Elo rating to validate
     * @return true if the Elo rating is valid, false otherwise
     */
    public boolean isValidElo(int elo);

    /**
     * Calculates the change in Elo rating based on the K-value, win value, and win probability.
     *
     * @param k the K-value
     * @param winValue the win value
     * @param winProbability the win probability
     * @return the change in Elo rating
     */
    public double eloChange(int k, double winValue, double winProbability);

    /**
     * Calculates the new Elo ratings for both players based on the outcome of a match.
     *
     * @param elo1 the Elo rating of the first player
     * @param elo2 the Elo rating of the second player
     * @param outcome the outcome of the match (0 for draw, 1 for player 2 wins, 2 for player 1 wins)
     * @return a list containing the new Elo ratings for both players
     */
    public List<Double> eloRatingForBoth(int elo1, int elo2, int outcome);
}
