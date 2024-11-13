package com.codewithcled.fullstack_backend_proj1.repository;

import java.util.List;
import com.codewithcled.fullstack_backend_proj1.model.Match;
import com.codewithcled.fullstack_backend_proj1.model.Round;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Match Repository
 */
@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    /**
     * Find all past matches that a player has completed
     * Find by checking if the match is complete and if the player is either player
     * 1 or player 2
     * 
     * @param isComplete  Will be true
     * @param player1id   id of player whose past matches are being searched for,
     *                    checked if player 1
     * @param isComplete2 Will be true
     * @param player2id   id of player whose past matches are being searched for,
     *                    checked if player 2
     * @return List of matches
     */
    List<Match> findByIsCompleteAndPlayer1OrIsCompleteAndPlayer2(Boolean isComplete, Long player1id,
            Boolean isComplete2, Long player2id);

    /**
     * Find the match that a player is in for a specific round
     * Find by checking in a round for a match where the player is either player 1
     * or player 2
     * 
     * @param round   Round that the match is in
     * @param player1 id of player
     * @param round2  Round that the match is in
     * @param player2 id of player
     * @return Match that the player is in for the given round
     */
    Match findByRoundAndPlayer1OrRoundAndPlayer2(Round round, Long player1, Round round2, Long player2);

}
