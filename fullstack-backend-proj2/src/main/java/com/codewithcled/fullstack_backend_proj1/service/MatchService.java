package com.codewithcled.fullstack_backend_proj1.service;

import com.codewithcled.fullstack_backend_proj1.model.Match;
import com.codewithcled.fullstack_backend_proj1.model.User;

/**
 * Service interface for managing matches.
 */
public interface MatchService {

    /**
     * Creates a new match between two players.
     *
     * @param player1 the first player
     * @param player2 the second player
     * @return the created match
     */
    Match createMatch(User player1, User player2);

    /**
     * Updates the result of a match.
     *
     * @param matchId the ID of the match to update
     * @param result the result of the match
     * @throws Exception if the match cannot be updated
     */
    void updateMatch(Long matchId, int result) throws Exception;

    /**
     * Gets the Elo rating change for the first player in a match.
     *
     * @param matchId the ID of the match
     * @return the Elo rating change for the first player
     * @throws Exception if the Elo change cannot be retrieved
     */
    Double getEloChange1(Long matchId) throws Exception;

    /**
     * Gets the Elo rating change for the second player in a match.
     *
     * @param matchId the ID of the match
     * @return the Elo rating change for the second player
     * @throws Exception if the Elo change cannot be retrieved
     */
    Double getEloChange2(Long matchId) throws Exception;

    /**
     * Gets the result of a match.
     *
     * @param matchId the ID of the match
     * @return the result of the match
     * @throws Exception if the result cannot be retrieved
     */
    int getResult(Long matchId) throws Exception;

    /**
     * Gets the usernames of the players in a match.
     *
     * @param matchId the ID of the match
     * @return an array containing the usernames of the players
     * @throws Exception if the usernames cannot be retrieved
     */
    String[] getPlayerUsernames(Long matchId) throws Exception;
}