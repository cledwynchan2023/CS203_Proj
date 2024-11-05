package com.codewithcled.fullstack_backend_proj1.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    //for debugging purposes
    private static final Logger logger = Logger.getLogger(MatchServiceImplementation.class.getName());

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

    @Autowired
    private RestTemplate restTemplate;

    // public RoundServiceImplementation(RoundRepository roundRepository){
    //     this.roundRepository = roundRepository;
    // }
    
    @Override
    public Round createFirstRound(Long tournamentId) throws Exception {

        //for debugging purposes
        logger.info("Create first round called");

        Tournament tournament = tournamentRepository.findById(tournamentId)
            .orElseThrow(() -> new Exception("Tournament not found"));
        List<User> participants = tournament.getParticipants();

        //for now, we only support even number of participants
        //ie throw exception for odd number of participants
        if(participants.size() % 2 != 0){
            throw new Exception("Number of participants must be even");
        }
        Round firstRound = new Round();
        firstRound.setRoundNum(1);
        firstRound.setTournament(tournament);

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
            Match match = matchService.createMatch(copy.get(i), copy.get(copy.size() / 2 + i));
            match.setRound(firstRound);
            matches.add(match);
        }

        //create scoreboard
        Map<Long, Double> newScoreboard = new LinkedHashMap<>();
        for(int i = 0; i < copy.size(); i++){
            newScoreboard.put(copy.get(i).getId(), 0.0);
        }
        
        firstRound.setScoreboard(newScoreboard);
        firstRound.setMatchList(matches);
        roundRepository.save(firstRound)
            .getMatchList()
            .forEach(match -> matchRepository.save(match));

        return firstRound;
    }

   
    @Override
    public void checkComplete(Long roundId) throws Exception {
        //for debugging purposes
        logger.info("Round check complete called");

        Round round = roundRepository.findById(roundId)
            .orElseThrow(() -> new Exception("Round not found"));

        logger.info("Round scoreboard at checkComplete: " + round.getScoreboard());

        boolean complete = true;
        for(Match match : round.getMatchList()){
            if(!match.getIsComplete()){
                complete = false;
                break;
            }
        }

        if(complete){
            //call roundService to check if round is complete
            round.setIsCompleted(true);

            Map<Long, Double> roundScoreboard = round.getScoreboard();
            List<Entry<Long, Double>> roundScoreboardList = new ArrayList<>(roundScoreboard.entrySet());
            logger.info("Round scoreboard: " + roundScoreboardList);

            // Tournament tournament = round.getTournament();
            // List<Round> rounds = tournament.getRounds();

            // ScoreboardComparator scoreboardComparator = new ScoreboardComparator(rounds, round, userRepository, matchRepository);
            // roundScoreboardList.sort(scoreboardComparator);

            // Map<Long, Double> sortedScoreboard = new LinkedHashMap<>();
            // for(Entry<Long, Double> entry: roundScoreboardList){
            //     sortedScoreboard.put(entry.getKey(), entry.getValue());
            // }
            // round.setScoreboard(sortedScoreboard);
            
            Tournament currentTournament = round.getTournament();
            Long currentTournamentId = currentTournament.getId();
            String relativeUrl = "/t/tournament/" + currentTournamentId + "/checkComplete";
            roundRepository.save(round);
            restTemplate.getForObject(relativeUrl, String.class);
        }
    }

    @Override
    public Round createNextRound(Long tournamentId) throws Exception {
        Tournament tournament = tournamentRepository.findById(tournamentId)
            .orElseThrow(() -> new Exception("Tournament not found"));

        Round newRound = new Round();
        newRound.setRoundNum(tournament.getRounds().size() + 1);
        newRound.setTournament(tournament);
        
        //get scoreboard from previous round to put as this round's scoreboard
        //and to set up matches
        Map<Long, Double> prevRoundScoreboard = tournament.getRounds().get(tournament.getRounds().size() - 1).getScoreboard();
        List<Entry<Long, Double>> prevRoundScoreboardList = new ArrayList<>(prevRoundScoreboard.entrySet());

        Map<Long, Double> newRoundScoreboard = new LinkedHashMap<>(prevRoundScoreboard);

        logger.info("Previous round scoreboard: " + prevRoundScoreboard);
        logger.info("New round scoreboard: " + newRoundScoreboard);

        newRound.setScoreboard(newRoundScoreboard);
        
        //pair up participants by score
        List<Match> matches = new ArrayList<>();
        for(int i = 0; i < prevRoundScoreboardList.size(); i += 2){
            User player1 = userRepository.findById(prevRoundScoreboardList.get(i).getKey())
                .orElseThrow(() -> new Exception("User not found"));
            User player2 = userRepository.findById(prevRoundScoreboardList.get(i + 1).getKey())
                .orElseThrow(() -> new Exception("User not found"));
            Match match = matchService.createMatch(player1, player2);
            match.setRound(newRound);
            matches.add(match);
        }
        newRound.setMatchList(matches);

        return roundRepository.save(newRound);
    }

    @Override
    public List<Match> getAllMatches(Long roundId) throws Exception {
        Round round = roundRepository.findById(roundId)
            .orElseThrow(() -> new Exception("Round not found"));
        return round.getMatchList();
    }
}
