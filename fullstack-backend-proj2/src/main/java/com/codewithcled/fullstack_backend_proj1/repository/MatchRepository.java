package com.codewithcled.fullstack_backend_proj1.repository;
import java.util.List;
import com.codewithcled.fullstack_backend_proj1.model.Match;
import com.codewithcled.fullstack_backend_proj1.model.Round;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    Match findByRound(Round round);

    //returns a player's past completed matches
    List<Match> findByIsCompleteAndPlayer1OrIsCompleteAndPlayer2(Boolean isComplete, Long player1id, Boolean isComplete2, Long player2id);
    
    Match findByRoundAndPlayer1OrRoundAndPlayer2(Round round, Long player1, Round round2, Long player2);

}
