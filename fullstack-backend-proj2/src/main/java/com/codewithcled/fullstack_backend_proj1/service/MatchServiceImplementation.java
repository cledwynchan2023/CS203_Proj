package com.codewithcled.fullstack_backend_proj1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private RoundService roundService;

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
    public void updateMatch(Long matchId, int result) throws Exception{
        Match currentMatch = matchRepository.findById(matchId)
        .orElseThrow(() -> new Exception("Match not found"));

        currentMatch.setResult(result);

        User player1 = currentMatch.getPlayer1();
        User player2 = currentMatch.getPlayer2();

        Double player1StartingElo = currentMatch.getPlayer1StartingElo();
        int player1StartingEloInt = player1StartingElo.intValue();

        Double player2StartingElo = currentMatch.getPlayer2StartingElo();
        int player2StartingEloInt = player2StartingElo.intValue();

        //elo calculations done here
        Double player1NewElo = eloRatingService.EloCalculation(player1StartingEloInt, player2StartingEloInt, result);
        Double eloChange1 = player1NewElo - player1StartingElo;

        Double player2NewElo = eloRatingService.EloCalculation(player2StartingEloInt, player1StartingEloInt, result);
        Double eloChange2 = player2NewElo - player2StartingElo;

        //save match results to match database
        currentMatch.setEloChange1(eloChange1);
        currentMatch.setEloChange2(eloChange2);
        currentMatch.setIsComplete(true);
        matchRepository.save(currentMatch);

        //still need to change the user's elo in the user database
        player1.setElo(player1NewElo);
        userRepository.save(player1);
        player2.setElo(player2NewElo);
        userRepository.save(player2);
        
        //todo: update tournament scoreboard here



        //call roundService to check if round is complete
        roundService.checkComplete(currentMatch.getRound().getId());
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