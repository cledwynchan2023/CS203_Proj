package com.codewithcled.fullstack_backend_proj1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.hc.client5.http.fluent.Request;
import java.util.Map;

import com.codewithcled.fullstack_backend_proj1.repository.MatchRepository;
import com.codewithcled.fullstack_backend_proj1.repository.TournamentRepository;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;
import com.codewithcled.fullstack_backend_proj1.model.Match;
import com.codewithcled.fullstack_backend_proj1.model.Round;
import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.model.User;

@Service
public class MatchServiceImplementation implements MatchService{
    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EloRatingService eloRatingService;

    @Override
    public Match createMatch(User player1, User player2){
        Match newMatch = new Match();
        newMatch.setPlayer1(player1.getId());
        newMatch.setPlayer2(player2.getId());
        newMatch.setPlayer1StartingElo(player1.getElo());
        newMatch.setPlayer2StartingElo(player2.getElo());
        return newMatch;
    }

    @Override
    public void updateMatch(Long matchId, int result) throws Exception{
        Match currentMatch = matchRepository.findById(matchId)
        .orElseThrow(() -> new Exception("Match not found"));

        if (currentMatch.getIsComplete()){
            throw new Exception("Match already complete, cannot update again");
        }
        
        currentMatch.setResult(result);
        currentMatch.setIsComplete(true);

        User player1 = userRepository.findById(currentMatch.getPlayer1())
            .orElseThrow(() -> new Exception("User not found"));
        User player2 = userRepository.findById(currentMatch.getPlayer2())
            .orElseThrow(() -> new Exception("User not found"));

        Double player1StartingElo = currentMatch.getPlayer1StartingElo();
        int player1StartingEloInt = player1StartingElo.intValue();

        Double player2StartingElo = currentMatch.getPlayer2StartingElo();
        int player2StartingEloInt = player2StartingElo.intValue();

        //elo calculations done here
        Double player1NewElo = eloRatingService.EloCalculation(player1StartingEloInt, player2StartingEloInt, result);
        Double eloChange1 = player1NewElo - player1StartingElo;

        Double player2NewElo = eloRatingService.EloCalculation(player2StartingEloInt, player1StartingEloInt, -1 * result);
        Double eloChange2 = player2NewElo - player2StartingElo;

        //save match results to match database
        currentMatch.setEloChange1(eloChange1);
        currentMatch.setEloChange2(eloChange2);
        currentMatch.setIsComplete(true);
        matchRepository.save(currentMatch);

        //save users' new elo in the user database
        player1.setElo(player1NewElo);
        userRepository.save(player1);
        player2.setElo(player2NewElo);
        userRepository.save(player2);
        
        //update tournament scoreboard in database
        Tournament currentTournament = currentMatch.getRound().getTournament();
        updateTournamentScoreboard(currentTournament, currentMatch, result);

        //call roundService to check if round is complete
        Round currentRound = currentMatch.getRound();
        Long currentRoundId = currentRound.getId();
        String url = "/round/" + currentRoundId + "/roundService/checkComplete";
        Request.get(url).execute().returnContent().asString();
        //roundService.checkComplete(currentMatch.getRound().getId());
    }

    public void updateTournamentScoreboard(Tournament currentTournament, Match currentMatch, int result){
        Map<Long, Double> scoreboard = currentTournament.getScoreboard();
        Long player1Id = currentMatch.getPlayer1();
        Long player2Id = currentMatch.getPlayer2();
        if (result == 0){
            //draw, scores for both players +0.5
            Double player1Score = scoreboard.get(player1Id);
            Double player2Score = scoreboard.get(player2Id);
            player1Score += 0.5;
            player2Score += 0.5;
            scoreboard.put(player1Id, player1Score);
            scoreboard.put(player2Id, player2Score);
        } else {
            //player 1 or player 2 win, winner score +1
            Long winnerId;
            if (result == 1){ //player 2 wins
                winnerId = player2Id;
            } else{ //player 1 wins
                winnerId = player1Id;
            }
            Double winnerScore = scoreboard.get(winnerId);
            winnerScore += 1;
            scoreboard.put(winnerId, winnerScore);
        }
        currentTournament.setScoreboard(scoreboard);
        tournamentRepository.save(currentTournament);
    }

    @Override
    public int getResult(Long matchId) throws Exception{
        Match currentMatch = matchRepository.findById(matchId)
        .orElseThrow(() -> new Exception("Match not found"));

        return currentMatch.getResult();
    }

    @Override
    public Double getEloChange1(Long matchId) throws Exception{
        Match currentMatch = matchRepository.findById(matchId)
        .orElseThrow(() -> new Exception("Match not found"));

        return currentMatch.getEloChange1();
    }

    @Override
    public Double getEloChange2(Long matchId) throws Exception{
        Match currentMatch = matchRepository.findById(matchId)
        .orElseThrow(() -> new Exception("Match not found"));

        return currentMatch.getEloChange2();
    }
}