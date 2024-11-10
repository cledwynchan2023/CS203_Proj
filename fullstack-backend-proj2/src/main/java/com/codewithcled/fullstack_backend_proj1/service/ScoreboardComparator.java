package com.codewithcled.fullstack_backend_proj1.service;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import com.codewithcled.fullstack_backend_proj1.model.Round;
import com.codewithcled.fullstack_backend_proj1.model.ScoreboardEntry;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.model.Match;
import com.codewithcled.fullstack_backend_proj1.repository.MatchRepository;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;

/**
 * Comparator for ScoreboardEntry objects.
 */
public class ScoreboardComparator implements Comparator<ScoreboardEntry> {
    private List<Round> rounds;
    private Round currentRound;
    private UserRepository userRepository;
    private MatchRepository matchRepository;

    /**
     * Constructor for ScoreboardComparator.
     *
     * @param rounds List of rounds
     * @param currentRound The current round
     * @param userRepository Repository for user data
     * @param matchRepository Repository for match data
     */
    public ScoreboardComparator(List<Round> rounds, Round currentRound, UserRepository userRepository, MatchRepository matchRepository) {
        this.rounds = rounds;
        this.currentRound = currentRound;
        this.userRepository = userRepository;
        this.matchRepository = matchRepository;
    }

    /**
     * Compares the scores of two ScoreboardEntry objects.
     *
     * @param e1 The first ScoreboardEntry
     * @param e2 The second ScoreboardEntry
     * @return A negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second
     */
    public int compareScores(ScoreboardEntry e1, ScoreboardEntry e2) {
        if (e1.getScore() > e2.getScore()) {
            return 1;
        } else if (e1.getScore() < e2.getScore()) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * Compares two ScoreboardEntry objects.
     *
     * @param e1 The first ScoreboardEntry
     * @param e2 The second ScoreboardEntry
     * @return A negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second
     */
    @Override
    public int compare(ScoreboardEntry e1, ScoreboardEntry e2) {
        if (compareScores(e1, e2) != 0) {
            return compareScores(e1, e2);
        }

        Long u1id = e1.getPlayerId();
        Long u2id = e2.getPlayerId();
        
        int tiebreakResult = tiebreak(u1id, u2id);
        if (tiebreakResult != 0) {
            return tiebreakResult;
        }

        return 0;
    }

    /**
     * Tiebreak method to compare two users.
     *
     * @param u1 The first user
     * @param u2 The second user
     * @return A negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second
     */
    public int tiebreak(Long user1id, Long user2id) {
        try {
            int solkoffTiebreakResult = solkoffTiebreak(user1id, user2id);
            if (solkoffTiebreakResult != 0) {
                return solkoffTiebreakResult;
            }

            int ratingTiebreakResult = ratingTiebreak(user1id, user2id);
            return ratingTiebreakResult;
        } catch (Exception e) {
            throw new RuntimeException("Error during tiebreak calculation", e);
        }
    }

    /**
     * Calculates the Solkoff median for a user.
     *
     * @param userId The user ID
     * @return The Solkoff median
     * @throws Exception If a match is not found
     */
    public double calculateSolkoffMedian(Long userId) throws Exception {
        double solkoff = 0;

        for (Round round : rounds) {
            Match match = matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(round, userId, round, userId);
            if (match == null) {
                throw new Exception("Match not found");
            } else {
                if (match.getPlayer1() == userId) {
                    solkoff += currentRound.getScoreboard().getPlayerScore(match.getPlayer2());
                } else {
                    solkoff += currentRound.getScoreboard().getPlayerScore(match.getPlayer1());
                }
            }
        }
        return solkoff;
    }

    /**
     * Solkoff tiebreak method to compare two users.
     *
     * @param u1 The first user
     * @param u2 The second user
     * @return A negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second
     * @throws Exception If an error occurs during calculation
     */
    public int solkoffTiebreak(Long user1id, Long user2id) throws Exception {
        double u1median = calculateSolkoffMedian(user1id);
        double u2median = calculateSolkoffMedian(user2id);

        if (u1median > u2median) {
            return 1;
        } else if (u1median < u2median) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * Calculates the total rating of opponents for a user.
     *
     * @param userId The user ID
     * @return The total rating of opponents
     * @throws Exception If a match is not found
     */
    public double calculateOpponentsTotalRating(Long userId) throws Exception {
        double rating = 0;

        for (Round round : rounds) {
            Match match = matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(round, userId, round, userId);
            if (match == null) {
                throw new Exception("Match not found");
            } else {
                if (match.getPlayer1() == userId) {
                    User opponent = userRepository.findById(match.getPlayer2()).get();
                    Double opponentElo = opponent.getElo();
                    rating += opponentElo;
                } else {
                    User opponent = userRepository.findById(match.getPlayer1()).get();
                    Double opponentElo = opponent.getElo();
                    rating += opponentElo;
                }
            }
        }
        return rating;
    }

    /**
     * Rating tiebreak method to compare two users.
     *
     * @param u1 The first user
     * @param u2 The second user
     * @return A negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second
     * @throws Exception If an error occurs during calculation
     */
    public int ratingTiebreak(Long user1id, Long user2id) throws Exception {
        User u1 = userRepository.findById(user1id).orElse(null);
        User u2 = userRepository.findById(user2id).orElse(null);

        if (u1 == null || u2 == null) {
            throw new NoSuchElementException("User not found");
        }

        Double u1TotalOpponentRatings = u2.getElo() + calculateOpponentsTotalRating(user1id);
        Double u2TotalOpponentRatings = u1.getElo() + calculateOpponentsTotalRating(user2id);

        if (u1TotalOpponentRatings > u2TotalOpponentRatings) {
            return 1;
        } else if (u1TotalOpponentRatings < u2TotalOpponentRatings) {
            return -1;
        } else {
            return 0;
        }
    }
}