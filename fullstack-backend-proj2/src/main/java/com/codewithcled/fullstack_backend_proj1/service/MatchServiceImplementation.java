package com.codewithcled.fullstack_backend_proj1.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.NoSuchElementException;

import com.codewithcled.fullstack_backend_proj1.repository.MatchRepository;
import com.codewithcled.fullstack_backend_proj1.repository.RoundRepository;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;
import com.codewithcled.fullstack_backend_proj1.config.ApplicationConfig;
import com.codewithcled.fullstack_backend_proj1.model.Match;
import com.codewithcled.fullstack_backend_proj1.model.Round;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.model.Scoreboard;

@Service
/**
 * {@inheritDoc}
 */
public class MatchServiceImplementation implements MatchService {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);
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
    /**
     * {@inheritDoc}
     */
    public Match createMatch(User player1, User player2) {
        Match newMatch = new Match();
        newMatch.setPlayer1(player1.getId());
        newMatch.setPlayer2(player2.getId());
        newMatch.setPlayer1StartingElo(player1.getElo());
        newMatch.setPlayer2StartingElo(player2.getElo());
        return newMatch;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public void updateMatch(Long matchId, int result) throws Exception {
        Match currentMatch = getMatchById(matchId);
        validateMatchIsNotComplete(currentMatch);

        User player1 = getUserById(currentMatch.getPlayer1());
        User player2 = getUserById(currentMatch.getPlayer2());

        Double player1StartingElo = currentMatch.getPlayer1StartingElo();
        Double player2StartingElo = currentMatch.getPlayer2StartingElo();

        List<Double> newElos = calculateNewElos(player1StartingElo, player2StartingElo, result);
        updateMatchAndUsers(currentMatch, player1, player2, result, newElos);

        updateRoundScoreboard(currentMatch.getRound(), currentMatch, result);
        checkIfRoundIsComplete(currentMatch.getRound().getId());
    }

    /**
     * Retrieves a match by its ID.
     *
     * @param matchId the ID of the match to retrieve
     * @return the match with the given ID
     * @throws NoSuchElementException if the match is not found
     */
    private Match getMatchById(Long matchId) throws NoSuchElementException {
        return matchRepository.findById(matchId)
                .orElseThrow(() -> new NoSuchElementException("Match not found"));
    }

    /**
     * Validates that the match is not complete.
     *
     * @param match the match to validate
     * @throws IllegalStateException if the match is already complete
     */
    private void validateMatchIsNotComplete(Match match) {
        if (match.getIsComplete()) {
            throw new IllegalStateException("Match already complete, cannot update again");
        }
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param userId the ID of the user to retrieve
     * @return the user with the given ID
     * @throws NoSuchElementException if the user is not found
     */
    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    /**
     * Calculates the new Elo ratings for both players based on the match result.
     *
     * @param player1StartingElo the starting Elo rating of player 1
     * @param player2StartingElo the starting Elo rating of player 2
     * @param result             the result of the match (0 for draw, 1 for player 2
     *                           wins, 2 for player 1 wins)
     * @return a list containing the new Elo ratings for both players
     */
    private List<Double> calculateNewElos(Double player1StartingElo, Double player2StartingElo, int result) {
        int player1StartingEloInt = player1StartingElo.intValue();
        int player2StartingEloInt = player2StartingElo.intValue();
        return eloRatingService.eloRatingForBoth(player1StartingEloInt, player2StartingEloInt, result);
    }

    /**
     * Updates the match and the users' Elo ratings based on the match result and
     * saves to the repository.
     *
     * @param match   the match to update
     * @param player1 the first player
     * @param player2 the second player
     * @param result  the result of the match
     * @param newElos the new Elo ratings for both players
     */
    private void updateMatchAndUsers(Match match, User player1, User player2, int result, List<Double> newElos) {
        Double player1NewElo = newElos.get(0);
        Double player2NewElo = newElos.get(1);
        Double eloChange1 = player1NewElo - match.getPlayer1StartingElo();
        Double eloChange2 = player2NewElo - match.getPlayer2StartingElo();

        match.setResult(result);
        match.setEloChange1(eloChange1);
        match.setEloChange2(eloChange2);
        match.setIsComplete(true);
        matchRepository.save(match);

        player1.setElo(player1NewElo);
        userRepository.save(player1);
        player2.setElo(player2NewElo);
        userRepository.save(player2);
    }

    /**
     * Sends a get request to roundController to check if round is complete.
     *
     * @param roundId the ID of the round to check
     */
    private void checkIfRoundIsComplete(Long roundId) {
        String relativeUrl = "/r/round/" + roundId + "/checkComplete";
        restTemplate.getForObject(relativeUrl, String.class);
    }

    /**
     * Updates the scoreboard for the current round based on the match result.
     *
     * @param currentRound the current round
     * @param currentMatch the current match
     * @param result       the result of the match
     * @throws Exception if the scoreboard cannot be updated
     */
    public void updateRoundScoreboard(Round currentRound, Match currentMatch, int result) throws Exception {
        Scoreboard currentRoundScoreboard = currentRound.getScoreboard();
        Long player1Id = currentMatch.getPlayer1();
        Long player2Id = currentMatch.getPlayer2();

        Double player1Score = getPlayerScore(currentRoundScoreboard, player1Id);
        Double player2Score = getPlayerScore(currentRoundScoreboard, player2Id);

        if (result == 0) {
            // Draw, scores for both players +0.5
            player1Score += 0.5;
            player2Score += 0.5;
        } else {
            // Player 1 or player 2 win, winner score +1
            if (result == 1) { // Player 2 wins
                player2Score += 1;
            } else { // Player 1 wins
                player1Score += 1;
            }
        }

        updateAndSortScoreboard(currentRoundScoreboard, player1Id, player1Score, player2Id, player2Score, currentRound);

        saveRoundAndLog(currentRound);
    }

    /**
     * Retrieves the score of a player from the scoreboard.
     *
     * @param scoreboard the scoreboard
     * @param playerId   the ID of the player
     * @return the score of the player
     * @throws RuntimeException if the player is not found in the scoreboard
     */
    private Double getPlayerScore(Scoreboard scoreboard, Long playerId) {
        Double score = scoreboard.getPlayerScore(playerId);
        if (score == null) {
            logger.error("Player not found in scoreboard");
            throw new RuntimeException("Player not found in scoreboard");
        }
        return score;
    }

    /**
     * Updates and sorts the scoreboard based on the players' scores.
     *
     * @param scoreboard   the scoreboard
     * @param player1Id    the ID of the first player
     * @param player1Score the score of the first player
     * @param player2Id    the ID of the second player
     * @param player2Score the score of the second player
     * @param currentRound the current round
     */
    private void updateAndSortScoreboard(Scoreboard scoreboard, Long player1Id, Double player1Score, Long player2Id,
            Double player2Score, Round currentRound) {
        scoreboard.updatePlayerScore(player1Id, player1Score);
        scoreboard.updatePlayerScore(player2Id, player2Score);

        ScoreboardComparator scoreboardComparator = new ScoreboardComparator(currentRound.getTournament().getRounds(),
                currentRound, userRepository, matchRepository);
        scoreboard.sortScoreboard(scoreboardComparator);

        logger.info("Sorted scoreboard: " + scoreboard.getScoreboardEntries());
    }

    /**
     * Saves the current round and logs the scoreboard.
     *
     * @param currentRound the current round
     */
    private void saveRoundAndLog(Round currentRound) {
        roundRepository.save(currentRound);
        logger.info("Checking Sorted Scoreboard after saving: " + currentRound.getScoreboard());
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public int getResult(Long matchId) throws Exception {
        Match currentMatch = getMatchById(matchId);

        return currentMatch.getResult();
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public Double getEloChange1(Long matchId) throws Exception {
        Match currentMatch = getMatchById(matchId);

        return currentMatch.getEloChange1();
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public Double getEloChange2(Long matchId) throws Exception {
        Match currentMatch = getMatchById(matchId);

        return currentMatch.getEloChange2();
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public String[] getPlayerUsernames(Long matchId) throws Exception {
        Match currentMatch = getMatchById(matchId);

        String[] players = new String[2];
        players[0] = userRepository.findById(currentMatch.getPlayer1())
                .orElseThrow(() -> new NoSuchElementException("Player 1 not found")).getUsername();
        players[1] = userRepository.findById(currentMatch.getPlayer2())
                .orElseThrow(() -> new NoSuchElementException("Player 2 not found")).getUsername();
        return players;
    }
}