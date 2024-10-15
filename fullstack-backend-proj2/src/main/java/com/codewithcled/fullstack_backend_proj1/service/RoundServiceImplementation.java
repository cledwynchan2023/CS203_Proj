package com.codewithcled.fullstack_backend_proj1.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.apache.hc.client5.http.fluent.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codewithcled.fullstack_backend_proj1.model.Round;
import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.model.Match;
import com.codewithcled.fullstack_backend_proj1.repository.MatchRepository;
import com.codewithcled.fullstack_backend_proj1.repository.RoundRepository;
import com.codewithcled.fullstack_backend_proj1.repository.TournamentRepository;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;

@Service
public class RoundServiceImplementation implements RoundService {

    @Autowired
    private RoundRepository roundRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MatchService matchService;

    public RoundServiceImplementation(RoundRepository roundRepository){
        this.roundRepository = roundRepository;
    }

    @Override
    public Round createFirstRound(List<User> participants) throws Exception {
        //for now, we only support even number of participants
        //ie throw exception for odd number of participants
        if(participants.size() % 2 != 0){
            throw new Exception("Number of participants must be even");
        }
        Round firstRound = new Round();
        firstRound.setRoundNum(1);
        List<User> copy = new ArrayList<>(participants);
        Collections.sort(copy, new Comparator<User>(){
            @Override
            public int compare(User u1, User u2){
                if(u1.getElo() > u2.getElo()){
                    return 1;
                }
                else if(u1.getElo() < u2.getElo()){
                    return -1;
                }
                else{
                    return 0;
                }
            }
        });
        List<Match> matches = new ArrayList<>();
        for(int i = 0; i < copy.size() / 2; i++){
            Match match = matchService.createMatch(copy.get(i), copy.get(copy.size() - i - 1));
            match.setRound(firstRound);
            matchRepository.save(match);
            matches.add(match);
        }
        firstRound.setMatchList(matches);

        return roundRepository.save(firstRound);
    }

    @Override
    public void checkComplete(Long roundId) throws Exception {
        Round round = roundRepository.findById(roundId)
            .orElseThrow(() -> new Exception("Round not found"));
        boolean complete = true;
        for(Match match : round.getMatchList()){
            if(!match.getIsComplete()){
                complete = false;
                break;
            }
        }

        if(complete){
            //call roundService to check if round is complete
            Tournament currentTournament = round.getTournament();
            Long currentTournamentId = currentTournament.getId();
            String url = "/tournament/" + currentTournamentId + "/tournamentService/checkComplete";
            Request.get(url).execute().returnContent().asString();
        }
    }

    @Override
    public Round createNextRound(Long tournamentId) throws Exception {
        Tournament tournament = tournamentRepository.findById(tournamentId)
            .orElseThrow(() -> new Exception("Tournament not found"));

        Round newRound = new Round();
        newRound.setRoundNum(tournament.getRounds().size() + 1);
        newRound.setTournament(tournament);

        Map<Long, Double> scoreboard = tournament.getScoreboard();
        List<Long> participantsId = new ArrayList<>();
        for(Long id : scoreboard.keySet()){
            participantsId.add(id);
        }
        participantsId.sort(new Comparator<Long>(){
            @Override
            public int compare(Long id1, Long id2){
                if(scoreboard.get(id1) > scoreboard.get(id2)){
                    return 1;
                }
                else if(scoreboard.get(id1) < scoreboard.get(id2)){
                    return -1;
                }
                else{
                    return 0;
                }
            }
        });
        List<Match> matches = new ArrayList<>();
        for(int i = 0; i < participantsId.size(); i += 2){
            User player1 = userRepository.findById(participantsId.get(i))
                .orElseThrow(() -> new Exception("User not found"));
            User player2 = userRepository.findById(participantsId.get(i + 1))
                .orElseThrow(() -> new Exception("User not found"));
            Match match = matchService.createMatch(player1, player2);
            match.setRound(newRound);
            matches.add(match);
        }
        newRound.setMatchList(matches);

        return roundRepository.save(newRound);
    }
}
