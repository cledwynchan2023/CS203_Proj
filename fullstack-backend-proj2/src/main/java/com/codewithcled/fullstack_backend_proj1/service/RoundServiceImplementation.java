package com.codewithcled.fullstack_backend_proj1.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.codewithcled.fullstack_backend_proj1.model.*;
import com.codewithcled.fullstack_backend_proj1.repository.*;

@Service
public class RoundServiceImplementation implements RoundService {

    //for debugging purposes
    private static final Logger logger = Logger.getLogger(RoundServiceImplementation.class.getName());

    public static class eloComparator implements Comparator<User>{
        @Override
        //sort by elo, ascending order
        public int compare(User u1, User u2){
            return Double.compare(u1.getElo(), u2.getElo());
        }
    }

    @Autowired
    private RoundRepository roundRepository;

    // @Autowired
    // private MatchRepository matchRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MatchService matchService;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * {@inheritDoc}
     */
    @Override
    public Round createFirstRound(Long tournamentId) throws Exception {

        //for debugging purposes
        logger.info("Create first round called");

        Tournament tournament = tournamentRepository.findById(tournamentId)
            .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        List<User> participants = tournament.getParticipants();

        //Currently only support even number of participants
        //Throw exception for odd number of participants
        if(participants.size() % 2 != 0){
            throw new IllegalArgumentException("Number of participants must be even");
        }

        //create first round and set round number and tournament
        Round firstRound = initializeNewRound(tournament);

        //create list containing participants, and sort by elo ascending order
        List<User> participantsList = new ArrayList<>(participants);
        Collections.sort(participantsList, new eloComparator());
        List<Match> matches = createFirstRoundMatches(participantsList, firstRound);

        //create scoreboard
        Scoreboard newScoreboard = new Scoreboard();
        List<ScoreboardEntry> newScoreboardEntryList = createNewScoreboardEntryList(participantsList, firstRound);
        newScoreboard.setScoreboardEntries(newScoreboardEntryList);

        //set this round's scoreboard and matchlist, then save round and matches to repository
        firstRound.setScoreboard(newScoreboard);
        firstRound.setMatchList(matches);
        roundRepository.save(firstRound);
        //     .getMatchList()
        //     .forEach(match -> matchRepository.save(match));

        return firstRound;
    }

    /**
     * Helper method to initialize a new round
     * Given a tournament, create a new round and set round number and tournament
     * 
     * @param tournament the tournament that the round will be in
     * @return the new round
     */
    private Round initializeNewRound(Tournament tournament) {
        Round newRound = new Round();
        newRound.setRoundNum(tournament.getRounds().size() + 1);
        newRound.setTournament(tournament);
        return newRound;
    }

    /**
     * Helper method to create first round matches
     * Given a list of participants sorted by elo, pair up the first half with the second half
     * 
     * @param participantsList list of participants sorted by elo
     * @param firstRound the first round that the matches will be in
     * @return list of matches, so that they can be saved to the repository
     */
    public List<Match> createFirstRoundMatches(List<User> participantsList, Round firstRound){
        List<Match> matches = new ArrayList<>();
        for(int i = 0; i < participantsList.size() / 2; i++){
            Match match = matchService.createMatch(participantsList.get(i), participantsList.get(participantsList.size() / 2 + i));
            match.setRound(firstRound);
            matches.add(match);
        }
        return matches;
    }

    /**
     * Helper method to create a new scoreboard entry list, for the first round
     * Given a list of participants, create a list of scoreboard entries with 0.0 score for all participants
     * 
     * @param participantsList list of participants
     * @param firstRound the first round that the scoreboard entries will be in
     * @return list of scoreboard entries, to add to the scoreboard
     */
    public List<ScoreboardEntry> createNewScoreboardEntryList(List<User> participantsList, Round firstRound){
        List<ScoreboardEntry> newScoreboardEntryList = new LinkedList<>();
        for(int i = 0; i < participantsList.size(); i++){
            ScoreboardEntry entry = new ScoreboardEntry(participantsList.get(i).getId(), 0.0);
            newScoreboardEntryList.add(entry);
        }
        return newScoreboardEntryList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkComplete(Long roundId) throws Exception {
        //for debugging purposes
        logger.info("Round check complete called");

        Round round = roundRepository.findById(roundId)
            .orElseThrow(() -> new NoSuchElementException("Round not found"));

        //for debugging purposes
        logger.info("Round scoreboard at checkComplete: " + round.getScoreboard());

        //check if all matches in round are complete
        boolean complete = areAllMatchesComplete(round);

        //if round is complete, set isCompleted to true and check if tournament is complete
        if(complete){
            completeRound(round);
        }
    }

    /**
     * Helper method to check if all matches in a round are complete
     * Given a round, check if all matches are complete
     * 
     * @param round the round to check
     * @return true if all matches are complete, false otherwise
     */
    private boolean areAllMatchesComplete(Round round) {
        for (Match match : round.getMatchList()) {
            if (!match.getIsComplete()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Helper method to begin round completion process
     * Given a round, set isCompleted to true and save to repository
     * Then request tournament check complete
     * 
     * @param round the round to complete
     */
    private void completeRound(Round round) {
        round.setIsCompleted(true);
        roundRepository.save(round);

        //for debugging purposes
        Scoreboard roundScoreboard = round.getScoreboard();
        logger.info("Round scoreboard: " + roundScoreboard.getScoreboardEntries());
        
        requestTournamentCheckComplete(round);
    }

    /**
     * Helper method to request tournament check complete
     * Given a completed round, make a GET request to the tournament controller
     * to check if the tournament is complete
     * 
     * @param round the round that is completed
     */
    private void requestTournamentCheckComplete(Round round) {
        Tournament currentTournament = round.getTournament();
        Long currentTournamentId = currentTournament.getId();
        String relativeUrl = "/t/tournament/" + currentTournamentId + "/checkComplete";
        restTemplate.getForObject(relativeUrl, String.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Round createNextRound(Long tournamentId) throws Exception {
        Tournament tournament = tournamentRepository.findById(tournamentId)
            .orElseThrow(() -> new NoSuchElementException("Tournament not found"));

        //create new round and set round number and tournament
        Round newRound = initializeNewRound(tournament);
        
        //get scoreboard from previous round to put as this round's scoreboard
        //and to set up matches
        Scoreboard newRoundScoreboard = createNextRoundScoreboard(tournament);
        newRound.setScoreboard(newRoundScoreboard);

        //for debugging purposes
        logger.info("Previous round scoreboard: " + newRoundScoreboard.getScoreboardEntries());
        
        //create new round's matches, pairing up participants by score
        List<Match> matches = createNextRoundMatches(newRound, newRoundScoreboard.getScoreboardEntries());
        newRound.setMatchList(matches);

        return roundRepository.save(newRound);
    }

    /**
     * Helper method to create next round scoreboard
     * Given the tournament, create a new scoreboard that carries over the previous round's scoreboard entries
     * 
     * @param tournament the tournament that the new round is in
     * @return the new scoreboard
     */
    private Scoreboard createNextRoundScoreboard(Tournament tournament) {
        Scoreboard prevRoundScoreboard = tournament.getRounds().get(tournament.getRounds().size() - 1).getScoreboard();
        List<ScoreboardEntry> prevRoundScoreboardEntries = prevRoundScoreboard.getScoreboardEntries();
        List<ScoreboardEntry> newRoundScoreboardEntries = new ArrayList<>(prevRoundScoreboardEntries);
        Scoreboard newScoreboard = new Scoreboard();
        newScoreboard.setScoreboardEntries(newRoundScoreboardEntries);
        return newScoreboard;
    }

    /**
     * Helper method to create next round matches
     * Given a new round and its scoreboard entries, pair up the participants by score adjacently
     * 
     * @param newRound the new round that the matches will be in
     * @param scoreboardEntries the round's scoreboard entries
     * @return list of matches, so that they can be saved to the repository
     */
    private List<Match> createNextRoundMatches(Round newRound, List<ScoreboardEntry> scoreboardEntries)
            throws Exception {
        List<Match> matches = new ArrayList<>();
        for(int i = 0; i < scoreboardEntries.size(); i += 2){
            User player1 = userRepository.findById(scoreboardEntries.get(i).getPlayerId())
                .orElseThrow(() -> new NoSuchElementException("User not found"));
            User player2 = userRepository.findById(scoreboardEntries.get(i + 1).getPlayerId())
                .orElseThrow(() -> new NoSuchElementException("User not found"));
            Match match = matchService.createMatch(player1, player2);
            match.setRound(newRound);
            matches.add(match);
        }
        return matches;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Match> getAllMatches(Long roundId) throws Exception {
        Round round = roundRepository.findById(roundId)
            .orElseThrow(() -> new NoSuchElementException("Round not found"));
        return round.getMatchList();
    }
}
