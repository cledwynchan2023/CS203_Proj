package com.codewithcled.fullstack_backend_proj1.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.codewithcled.fullstack_backend_proj1.model.Round;
import com.codewithcled.fullstack_backend_proj1.model.Scoreboard;
import com.codewithcled.fullstack_backend_proj1.model.ScoreboardEntry;
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

    public class eloComparator implements Comparator<User>{
        @Override
        //sort by elo, ascending order
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
    }

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
    public List<Match> createMatches(List<User> participantsList, Round firstRound){
        List<Match> matches = new ArrayList<>();
        for(int i = 0; i < participantsList.size() / 2; i++){
            Match match = matchService.createMatch(participantsList.get(i), participantsList.get(participantsList.size() / 2 + i));
            match.setRound(firstRound);
            matches.add(match);
        }
        return matches;
    }

    public List<ScoreboardEntry> createScoreboardEntryList(List<User> participantsList, Round firstRound){
        List<ScoreboardEntry> newScoreboardEntryList = new LinkedList<>();
        for(int i = 0; i < participantsList.size(); i++){
            ScoreboardEntry entry = new ScoreboardEntry(participantsList.get(i).getId(), 0.0);
            newScoreboardEntryList.add(entry);
        }
        return newScoreboardEntryList;
    }

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

        //create list containing participants, and sort by elo ascending order
        List<User> participantsList = new ArrayList<>(participants);
        Collections.sort(participantsList, new eloComparator());
        List<Match> matches = createMatches(participantsList, firstRound);

        //create scoreboard
        Scoreboard newScoreboard = new Scoreboard();
        List<ScoreboardEntry> newScoreboardEntryList = createScoreboardEntryList(participantsList, firstRound);
        newScoreboard.setEntries(newScoreboardEntryList);

        //set this round's scoreboard and matchlist, then save to repository
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
            roundRepository.save(round);

            Scoreboard roundScoreboard = round.getScoreboard();
            logger.info("Round scoreboard: " + roundScoreboard.getScoreboardEntries());
            
            Tournament currentTournament = round.getTournament();
            Long currentTournamentId = currentTournament.getId();
            String relativeUrl = "/t/tournament/" + currentTournamentId + "/checkComplete";
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
        Scoreboard prevRoundScoreboard = tournament.getRounds().get(tournament.getRounds().size() - 1).getScoreboard();
        List<ScoreboardEntry> prevRoundScoreboardEntries = prevRoundScoreboard.getScoreboardEntries();
        List<ScoreboardEntry> newRoundScoreboardEntries = new ArrayList<>(prevRoundScoreboardEntries);
        
        Scoreboard newRoundScoreboard = new Scoreboard();
        newRoundScoreboard.setEntries(newRoundScoreboardEntries);
        newRound.setScoreboard(newRoundScoreboard);

        logger.info("Previous round scoreboard: " + prevRoundScoreboardEntries);
        logger.info("New round scoreboard: " + newRoundScoreboardEntries);
        
        //pair up participants by score
        List<Match> matches = new ArrayList<>();
        for(int i = 0; i < prevRoundScoreboardEntries.size(); i += 2){
            User player1 = userRepository.findById(prevRoundScoreboardEntries.get(i).getPlayerId())
                .orElseThrow(() -> new Exception("User not found"));
            User player2 = userRepository.findById(prevRoundScoreboardEntries.get(i + 1).getPlayerId())
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
