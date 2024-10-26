package com.codewithcled.fullstack_backend_proj1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.List;

import com.codewithcled.fullstack_backend_proj1.repository.MatchRepository;
import com.codewithcled.fullstack_backend_proj1.repository.RoundRepository;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;
import com.codewithcled.fullstack_backend_proj1.model.Match;
import com.codewithcled.fullstack_backend_proj1.model.Round;
import com.codewithcled.fullstack_backend_proj1.model.User;

@Service
public class MatchServiceImplementation implements MatchService{
    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private RoundRepository roundRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EloRatingService eloRatingService;

    @Autowired
    private RestTemplate restTemplate;

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

        User player1 = userRepository.findById(currentMatch.getPlayer1())
            .orElseThrow(() -> new Exception("User not found"));
        User player2 = userRepository.findById(currentMatch.getPlayer2())
            .orElseThrow(() -> new Exception("User not found"));

        Double player1StartingElo = currentMatch.getPlayer1StartingElo();
        int player1StartingEloInt = player1StartingElo.intValue();

        Double player2StartingElo = currentMatch.getPlayer2StartingElo();
        int player2StartingEloInt = player2StartingElo.intValue();

        //elo calculations done here
        List<Double> newElos = eloRatingService.eloRatingForBoth(player1StartingEloInt, player2StartingEloInt, result);
        Double player1NewElo = newElos.get(0);
        Double eloChange1 = player1NewElo - player1StartingElo;
        Double player2NewElo = newElos.get(1);
        Double eloChange2 = player2NewElo - player2StartingElo;

        //save match results to match database
        currentMatch.setResult(result);
        currentMatch.setEloChange1(eloChange1);
        currentMatch.setEloChange2(eloChange2);
        currentMatch.setIsComplete(true);
        matchRepository.save(currentMatch);

        //save users' new elo in the user database
        player1.setElo(player1NewElo);
        userRepository.save(player1);
        player2.setElo(player2NewElo);
        userRepository.save(player2);
        
        //update round scoreboard in database
        Round currentRound = currentMatch.getRound();
        updateRoundScoreboard(currentRound, currentMatch, result);

        //call roundService to check if round is complete
        Long currentRoundId = currentRound.getId();
        String relativeUrl = "/r/round/" + currentRoundId + "/checkComplete";
        restTemplate.getForObject(relativeUrl, String.class);
        //roundService.checkComplete(currentMatch.getRound().getId());
    }

    public void updateRoundScoreboard(Round currentRound, Match currentMatch, int result){
        Map<Long, Double> scoreboard = currentRound.getScoreboard();
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
        currentRound.setScoreboard(scoreboard);
        roundRepository.save(currentRound);
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