package com.codewithcled.fullstack_backend_proj1.service;

import java.util.List;
import com.codewithcled.fullstack_backend_proj1.model.Match;
import com.codewithcled.fullstack_backend_proj1.model.Round;

public interface RoundService {
    /**
     * Creates the first round for a tournament.
     *
     * @param tournamentId the ID of the tournament
     * @return the created,first Round
     * @throws Exception if an error occurs
     */
    Round createFirstRound(Long tournamentId) throws Exception;

    /**
     * Checks if a round is complete.
     * Method is called after a match is updated.
     *
     * @param roundId the ID of the round
     * @throws Exception if an error occurs
     */
    void checkComplete(Long roundId) throws Exception;

    /**
     * Creates the next round for a tournament.
     * Method is called after a round is complete, but the tournament is not.
     *
     * @param tournamentId the ID of the tournament
     * @return the created next Round
     * @throws Exception if an error occurs
     */
    Round createNextRound(Long tournamentId) throws Exception;

    /**
     * Gets all matches for a round.
     *
     * @param tournamentId the ID of the tournament
     * @return the list of matches
     * @throws Exception if an error occurs
     */
    List<Match> getAllMatches(Long tournamentId) throws Exception;
}
