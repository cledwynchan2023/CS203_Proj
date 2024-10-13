package com.codewithcled.fullstack_backend_proj1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codewithcled.fullstack_backend_proj1.repository.MatchRepository;
import com.codewithcled.fullstack_backend_proj1.repository.TournamentRepository;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;
import com.codewithcled.fullstack_backend_proj1.model.Match;
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
    public Match createMatch(User player1, User player2) throws Exception{
        Match newMatch = new Match();
        newMatch.setPlayer1(player1);
        newMatch.setPlayer2(player2);
        newMatch.setPlayer1StartingElo(player1.getElo());
        newMatch.setPlayer2StartingElo(player2.getElo());
        return matchRepository.save(newMatch);
    }

    @Override
    public void updateMatch(Long matchId, int result){
        Match currentMatch = matchRepository.findById(matchId)
        .orElseThrow(() -> new Exception("Match not found"));

        currentMatch.setResult(result);

        Double player1StartingElo = currentMatch.getPlayer1StartingElo();
        Double player2StartingElo = currentMatch.getPlayer2StartingElo();

        //todo: implement elo changes here
        Double eloChange1 = eloRatingService.EloCalculation(player1StartingElo, player2StartingElo, result);
        Double eloChange2 = eloRatingService.EloCalculation(player2StartingElo, player1StartingElo, result);
        
        currentMatch.setEloChange1(eloChange1);
        currentMatch.setEloChange2(eloChange2);
        currentMatch.setIsComplete(true);

        //still need to change the user's elo in the user database
        player1.setElo(player1StartingElo + eloChange1);
        userRepository.save(player1);
        player2.setElo(player2StartingElo + eloChange2);
        userRepository.save(player2);

        matchRepository.save(currentMatch);
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