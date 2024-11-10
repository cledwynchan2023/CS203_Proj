package com.codewithcled.fullstack_backend_proj1.UnitTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.codewithcled.fullstack_backend_proj1.model.Match;
import com.codewithcled.fullstack_backend_proj1.model.Round;
import com.codewithcled.fullstack_backend_proj1.model.Scoreboard;
import com.codewithcled.fullstack_backend_proj1.model.ScoreboardEntry;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.repository.MatchRepository;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;
import com.codewithcled.fullstack_backend_proj1.service.ScoreboardComparator;

import jakarta.annotation.Resource;

public class scoreBoardSortTest {
        @Mock
        private UserRepository userRepository;

        @Mock
        private MatchRepository matchRepository;

        @InjectMocks
        @Resource
        private ScoreboardComparator scoreboardComparator;

        @SuppressWarnings("deprecation")
        @BeforeEach
        public void setUp() throws Exception {
                // Initialize mocks created above
                MockitoAnnotations.initMocks(this);
        }

        @Test
        void solkoffTiebreak_Success_u1MedianScoreGreater_Return1() throws Exception {
                Long uId1 = (long) 1;
                Long uId2 = (long) 2;
                User user1 = new User();
                user1.setId(uId1);
                User user2 = new User();
                user2.setId(uId2);
                List<Round> rounds = new ArrayList<>();
                Round testRound = new Round();

                Scoreboard scoreboard = new Scoreboard();
                List<ScoreboardEntry> scoreboardEntrys = new ArrayList<>();
                ScoreboardEntry entry1 = new ScoreboardEntry(uId1, 0.0); // Means that player 1 and player 2 played
                                                                         // against each other.
                                                                         // Since player 1 beat player 2, player1 is
                                                                         // tougher opponent
                                                                         // If score same at the end the player who
                                                                         // fought tougher opponents wins
                ScoreboardEntry entry2 = new ScoreboardEntry(uId2, 1.0);
                scoreboardEntrys.add(entry1);
                scoreboardEntrys.add(entry2);
                scoreboard.setScoreboardEntries(scoreboardEntrys);

                testRound.setScoreboard(scoreboard);

                Match testMatch = new Match();
                testMatch.setRound(testRound);
                testMatch.setPlayer1(uId1);
                testMatch.setPlayer2(uId2);
                rounds.add(testRound);

                when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1))
                                .thenReturn(testMatch);
                when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId2, testRound, uId2))
                                .thenReturn(testMatch);

                scoreboardComparator=new ScoreboardComparator(rounds, testRound, userRepository, matchRepository);

                int result = scoreboardComparator.solkoffTiebreak(user1.getId(), user2.getId());

                assertEquals(1, result);
                verify(matchRepository).findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1);
                verify(matchRepository).findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId2, testRound, uId2);
        }

        @Test
        void solkoffTiebreak_Success_u1OppScoreLesser_ReturnNeg1() throws Exception {
                Long uId1 = (long) 1;
                Long uId2 = (long) 2;

                User user1 = new User();
                user1.setId(uId1);

                User user2 = new User();
                user2.setId(uId2);
                List<Round> rounds = new ArrayList<>();
                Round testRound = new Round();

                Scoreboard scoreboard = new Scoreboard();
                List<ScoreboardEntry> scoreboardEntrys = new ArrayList<>();
                ScoreboardEntry entry1 = new ScoreboardEntry(uId1, 1.0);
                ScoreboardEntry entry2 = new ScoreboardEntry(uId2, 0.0);
                scoreboardEntrys.add(entry1);
                scoreboardEntrys.add(entry2);
                scoreboard.setScoreboardEntries(scoreboardEntrys);

                testRound.setScoreboard(scoreboard);

                Match testMatch = new Match();
                testMatch.setRound(testRound);
                testMatch.setPlayer1(uId1);
                testMatch.setPlayer2(uId2);
                rounds.add(testRound);

                when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1))
                                .thenReturn(testMatch);
                when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId2, testRound, uId2))
                                .thenReturn(testMatch);

                scoreboardComparator=new ScoreboardComparator(rounds, testRound, userRepository, matchRepository);
                int result = scoreboardComparator.solkoffTiebreak(user1.getId(), user2.getId());

                assertEquals(-1, result);
                verify(matchRepository).findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1);
                verify(matchRepository).findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId2, testRound, uId2);
        }

        @Test
        void solkoffTiebreak_Success_u1OppScoreEqual_Return0() throws Exception {
                Long uId1 = (long) 1;
                Long uId2 = (long) 2;
                User user1 = new User();
                user1.setId(uId1);
                User user2 = new User();
                user2.setId(uId2);
                List<Round> rounds = new ArrayList<>();
                Round testRound = new Round();

                Scoreboard scoreboard = new Scoreboard();
                List<ScoreboardEntry> scoreboardEntrys = new ArrayList<>();
                ScoreboardEntry entry1 = new ScoreboardEntry(uId1, 0.0);
                ScoreboardEntry entry2 = new ScoreboardEntry(uId2, 0.0);
                scoreboardEntrys.add(entry1);
                scoreboardEntrys.add(entry2);
                scoreboard.setScoreboardEntries(scoreboardEntrys);

                testRound.setScoreboard(scoreboard);

                Match testMatch = new Match();
                testMatch.setRound(testRound);
                testMatch.setPlayer1(uId1);
                testMatch.setPlayer2(uId2);
                rounds.add(testRound);

                when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1))
                                .thenReturn(testMatch);
                when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId2, testRound, uId2))
                                .thenReturn(testMatch);

                scoreboardComparator=new ScoreboardComparator(rounds, testRound, userRepository, matchRepository);
                int result = scoreboardComparator.solkoffTiebreak(user1.getId(), user2.getId());

                assertEquals(0, result);
                verify(matchRepository).findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1);
                verify(matchRepository).findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId2, testRound, uId2);
        }

        @Test
        void solkoffTiebreak_Success_u1OppScoreEqual_SwapPlayers_Return0() throws Exception {
                Long uId1 = (long) 1;
                Long uId2 = (long) 2;
                User user1 = new User();
                user1.setId(uId2);
                User user2 = new User();
                user2.setId(uId1);
                List<Round> rounds = new ArrayList<>();
                Round testRound = new Round();

                Scoreboard scoreboard = new Scoreboard();
                List<ScoreboardEntry> scoreboardEntrys = new ArrayList<>();
                ScoreboardEntry entry1 = new ScoreboardEntry(uId1, 0.0);
                ScoreboardEntry entry2 = new ScoreboardEntry(uId2, 0.0);
                scoreboardEntrys.add(entry1);
                scoreboardEntrys.add(entry2);
                scoreboard.setScoreboardEntries(scoreboardEntrys);

                testRound.setScoreboard(scoreboard);

                Match testMatch = new Match();
                testMatch.setRound(testRound);
                testMatch.setPlayer1(uId1);
                testMatch.setPlayer2(uId2);
                rounds.add(testRound);

                when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1))
                                .thenReturn(testMatch);
                when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId2, testRound, uId2))
                                .thenReturn(testMatch);

                scoreboardComparator=new ScoreboardComparator(rounds, testRound, userRepository, matchRepository);
                int result = scoreboardComparator.solkoffTiebreak(user1.getId(), user2.getId());

                assertEquals(0, result);
                verify(matchRepository).findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1);
                verify(matchRepository).findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId2, testRound, uId2);
        }

        @Test
        void solkoffTiebreak_Failure_MissingMatchForPlayer1_ReturnException() throws Exception {
                Long uId1 = (long) 1;
                Long uId2 = (long) 2;
                User user1 = new User();
                user1.setId(uId1);
                User user2 = new User();
                user2.setId(uId2);
                List<Round> rounds = new ArrayList<>();
                Round testRound = new Round();

                Scoreboard scoreboard = new Scoreboard();
                List<ScoreboardEntry> scoreboardEntrys = new ArrayList<>();
                ScoreboardEntry entry1 = new ScoreboardEntry(uId1, 0.0);
                ScoreboardEntry entry2 = new ScoreboardEntry(uId2, 0.0);
                scoreboardEntrys.add(entry1);
                scoreboardEntrys.add(entry2);
                scoreboard.setScoreboardEntries(scoreboardEntrys);
                testRound.setScoreboard(scoreboard);

                Match testMatch = new Match();
                testMatch.setRound(testRound);
                testMatch.setPlayer1(uId1);
                testMatch.setPlayer2(uId2);
                rounds.add(testRound);

                when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1))
                                .thenReturn(null);

                scoreboardComparator=new ScoreboardComparator(rounds, testRound, userRepository, matchRepository);
                Exception exception = assertThrows(Exception.class, () -> {
                        scoreboardComparator.solkoffTiebreak(user1.getId(), user2.getId());
                });

                assertEquals("Match not found", exception.getMessage());
                verify(matchRepository).findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1);
                verify(matchRepository, never()).findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId2, testRound,
                                uId2);
        }

        @Test
        void solkoffTiebreak_Failure_MissingMatchForPlayer2_ReturnException() throws Exception {
                Long uId1 = (long) 1;
                Long uId2 = (long) 2;
                User user1 = new User();
                user1.setId(uId1);
                User user2 = new User();
                user2.setId(uId2);
                List<Round> rounds = new ArrayList<>();
                Round testRound = new Round();

                Scoreboard scoreboard = new Scoreboard();
                List<ScoreboardEntry> scoreboardEntrys = new ArrayList<>();
                ScoreboardEntry entry1 = new ScoreboardEntry(uId1, 0.0);
                ScoreboardEntry entry2 = new ScoreboardEntry(uId2, 0.0);
                scoreboardEntrys.add(entry1);
                scoreboardEntrys.add(entry2);
                scoreboard.setScoreboardEntries(scoreboardEntrys);
                testRound.setScoreboard(scoreboard);

                Match testMatch = new Match();
                testMatch.setRound(testRound);
                testMatch.setPlayer1(uId1);
                testMatch.setPlayer2(uId2);
                rounds.add(testRound);

                when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1))
                                .thenReturn(testMatch);
                when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId2, testRound, uId2))
                                .thenReturn(null);

                scoreboardComparator=new ScoreboardComparator(rounds, testRound, userRepository, matchRepository);
                Exception exception = assertThrows(Exception.class, () -> {
                        scoreboardComparator.solkoffTiebreak(user1.getId(), user2.getId());
                });

                assertEquals("Match not found", exception.getMessage());
                verify(matchRepository).findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1);
                verify(matchRepository).findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId2, testRound, uId2);
        }

        @Test
        void ratingTiebreak_Success_Player1OppEloGreater_ReturnNeg1() throws Exception {
                Long uId1 = (long) 1;
                Long uId2 = (long) 2;
                Long uId3 = (long) 3;
                Long uId4 = (long) 4;
                Double elo1 = (double) 1000;
                Double elo2 = (double) 3000;
                Double elo3 = (double) 4000;

                User user1 = new User();
                user1.setId(uId1);
                user1.setElo(elo1);

                User user2 = new User();
                user2.setId(uId2);
                user2.setElo(elo1);

                User opp1 = new User();
                opp1.setId(uId3);
                opp1.setElo(elo2);

                User opp2 = new User();
                opp2.setId(uId4);
                opp2.setElo(elo3);

                List<Round> rounds = new ArrayList<>();
                Round testRound = new Round();

                Match testMatch1 = new Match();
                testMatch1.setRound(testRound);
                testMatch1.setPlayer1(uId1);// opp elo 4000
                testMatch1.setPlayer2(uId4);

                Match testMatch2 = new Match();
                testMatch2.setRound(testRound);
                testMatch2.setPlayer1(uId2);// opp elo 3000
                testMatch2.setPlayer2(uId3);
                rounds.add(testRound);

                when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1))
                                .thenReturn(testMatch1);
                when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId2, testRound, uId2))
                                .thenReturn(testMatch2);
                when(userRepository.findById(uId1)).thenReturn(Optional.of(user1));
                when(userRepository.findById(uId2)).thenReturn(Optional.of(user2));
                when(userRepository.findById(uId3)).thenReturn(Optional.of(opp1));
                when(userRepository.findById(uId4)).thenReturn(Optional.of(opp2));

                scoreboardComparator=new ScoreboardComparator(rounds, testRound, userRepository, matchRepository);
                int result = scoreboardComparator.ratingTiebreak(user1.getId(), user2.getId());

                assertEquals(-1, result);
                verify(matchRepository).findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1);
                verify(matchRepository).findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId2, testRound, uId2);
                verify(userRepository).findById(uId3);
                verify(userRepository).findById(uId4);
        }

        @Test
        void ratingTiebreak_Success_Player1OppEloLesser_Return1() throws Exception {
                Long uId1 = (long) 1;
                Long uId2 = (long) 2;
                Long uId3 = (long) 3;
                Long uId4 = (long) 4;
                Double elo1 = (double) 1000;
                Double elo2 = (double) 3000;
                Double elo3 = (double) 4000;

                User user1 = new User();
                user1.setId(uId1);
                user1.setElo(elo1);

                User user2 = new User();
                user2.setId(uId2);
                user2.setElo(elo1);

                User opp1 = new User();
                opp1.setId(uId3);
                opp1.setElo(elo2);

                User opp2 = new User();
                opp2.setId(uId4);
                opp2.setElo(elo3);

                List<Round> rounds = new ArrayList<>();
                Round testRound = new Round();

                Match testMatch1 = new Match();
                testMatch1.setRound(testRound);
                testMatch1.setPlayer1(uId1);// opp elo 3000
                testMatch1.setPlayer2(uId3);

                Match testMatch2 = new Match();
                testMatch2.setRound(testRound);
                testMatch2.setPlayer1(uId2);// opp elo 4000
                testMatch2.setPlayer2(uId4);
                rounds.add(testRound);

                when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1))
                                .thenReturn(testMatch1);
                when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId2, testRound, uId2))
                                .thenReturn(testMatch2);
                when(userRepository.findById(uId1)).thenReturn(Optional.of(user1));
                when(userRepository.findById(uId2)).thenReturn(Optional.of(user2));
                when(userRepository.findById(uId3)).thenReturn(Optional.of(opp1));
                when(userRepository.findById(uId4)).thenReturn(Optional.of(opp2));

                scoreboardComparator=new ScoreboardComparator(rounds, testRound, userRepository, matchRepository);
                int result = scoreboardComparator.ratingTiebreak(user1.getId(), user2.getId());

                assertEquals(1, result);

                verify(matchRepository).findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1);
                verify(matchRepository).findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId2, testRound, uId2);
                verify(userRepository).findById(uId3);
                verify(userRepository).findById(uId4);
        }

        @Test
        void ratingTiebreak_Success_Player1OppEloLesser_WorksWhenSwappingOpponents_Return1() throws Exception {
                Long uId1 = (long) 1;
                Long uId2 = (long) 2;
                Long uId3 = (long) 3;
                Long uId4 = (long) 4;
                Double elo1 = (double) 1000;
                Double elo2 = (double) 3000;
                Double elo3 = (double) 4000;

                User user1 = new User();
                user1.setId(uId1);
                user1.setElo(elo1);

                User user2 = new User();
                user2.setId(uId2);
                user2.setElo(elo1);

                User opp1 = new User();
                opp1.setId(uId3);
                opp1.setElo(elo2);

                User opp2 = new User();
                opp2.setId(uId4);
                opp2.setElo(elo3);

                List<Round> rounds = new ArrayList<>();
                Round testRound = new Round();

                Match testMatch1 = new Match();
                testMatch1.setRound(testRound);
                testMatch1.setPlayer1(uId3);
                testMatch1.setPlayer2(uId1);

                Match testMatch2 = new Match();
                testMatch2.setRound(testRound);
                testMatch2.setPlayer1(uId4);
                testMatch2.setPlayer2(uId2);
                rounds.add(testRound);

                when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1))
                                .thenReturn(testMatch1);
                when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId2, testRound, uId2))
                                .thenReturn(testMatch2);
                when(userRepository.findById(uId1)).thenReturn(Optional.of(user1));
                when(userRepository.findById(uId2)).thenReturn(Optional.of(user2));
                when(userRepository.findById(uId3)).thenReturn(Optional.of(opp1));
                when(userRepository.findById(uId4)).thenReturn(Optional.of(opp2));

                scoreboardComparator=new ScoreboardComparator(rounds, testRound, userRepository, matchRepository);
                int result = scoreboardComparator.ratingTiebreak(user1.getId(), user2.getId());

                assertEquals(1, result);

                verify(matchRepository).findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1);
                verify(matchRepository).findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId2, testRound, uId2);
                verify(userRepository).findById(uId3);
                verify(userRepository).findById(uId4);
        }

        @Test
        void ratingTiebreak_Success_Player1OppEloEquals_Return0() throws Exception {
                Long uId1 = (long) 1;
                Long uId2 = (long) 2;
                Double elo1 = (double) 1000;

                User user1 = new User();
                user1.setId(uId1);
                user1.setElo(elo1);

                User user2 = new User();
                user2.setId(uId2);
                user2.setElo(elo1);

                List<Round> rounds = new ArrayList<>();
                Round testRound = new Round();

                Scoreboard scoreboard = new Scoreboard();
                List<ScoreboardEntry> scoreboardEntrys = new ArrayList<>();
                ScoreboardEntry entry1 = new ScoreboardEntry(uId1, 0.0);
                ScoreboardEntry entry2 = new ScoreboardEntry(uId2, 0.0);
                scoreboardEntrys.add(entry1);
                scoreboardEntrys.add(entry2);
                scoreboard.setScoreboardEntries(scoreboardEntrys);
                testRound.setScoreboard(scoreboard);

                Match testMatch = new Match();
                testMatch.setRound(testRound);
                testMatch.setPlayer1(uId1);
                testMatch.setPlayer2(uId2);
                rounds.add(testRound);

                when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1))
                                .thenReturn(testMatch);
                when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId2, testRound, uId2))
                                .thenReturn(testMatch);
                when(userRepository.findById(uId1)).thenReturn(Optional.of(user1));
                when(userRepository.findById(uId2)).thenReturn(Optional.of(user2));

                scoreboardComparator=new ScoreboardComparator(rounds, testRound, userRepository, matchRepository);
                int result = scoreboardComparator.ratingTiebreak(user1.getId(), user2.getId());

                assertEquals(0, result);
                verify(matchRepository).findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1);
                verify(matchRepository).findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId2, testRound, uId2);
                verify(userRepository,times(2)).findById(uId1);
                verify(userRepository,times(2)).findById(uId2);
        }

        @Test
        void ratingTiebreak_Failure_Player1MatchNotFound_ReturnException() throws Exception {
                Long uId1 = (long) 1;
                Long uId2 = (long) 2;
                Double elo1 = (double) 1000;

                User user1 = new User();
                user1.setId(uId1);
                user1.setElo(elo1);

                User user2 = new User();
                user2.setId(uId2);
                user2.setElo(elo1);

                List<Round> rounds = new ArrayList<>();
                Round testRound = new Round();

                Scoreboard scoreboard = new Scoreboard();
                List<ScoreboardEntry> scoreboardEntrys = new ArrayList<>();
                ScoreboardEntry entry1 = new ScoreboardEntry(uId1, 0.0);
                ScoreboardEntry entry2 = new ScoreboardEntry(uId2, 0.0);
                scoreboardEntrys.add(entry1);
                scoreboardEntrys.add(entry2);
                scoreboard.setScoreboardEntries(scoreboardEntrys);
                testRound.setScoreboard(scoreboard);

                Match testMatch = new Match();
                testMatch.setRound(testRound);
                testMatch.setPlayer1(uId1);
                testMatch.setPlayer2(uId2);
                rounds.add(testRound);

                when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1))
                                .thenReturn(null);
                when(userRepository.findById(uId1)).thenReturn(Optional.of(user1));
                when(userRepository.findById(uId2)).thenReturn(Optional.of(user2));

                scoreboardComparator=new ScoreboardComparator(rounds, testRound, userRepository, matchRepository);
                Exception exception = assertThrows(Exception.class, () -> {
                        scoreboardComparator.ratingTiebreak(user1.getId(), user2.getId());
                });

                assertEquals("Match not found", exception.getMessage());
                verify(matchRepository).findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1);
                verify(matchRepository, never()).findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId2, testRound,
                                uId2);
                verify(userRepository).findById(uId1);
                verify(userRepository).findById(uId2);
        }

        @Test
        void ratingTiebreak_Failure_Player2MatchNotFound_ReturnException() throws Exception {
                Long uId1 = (long) 1;
                Long uId2 = (long) 2;
                Double elo1 = (double) 1000;

                User user1 = new User();
                user1.setId(uId1);
                user1.setElo(elo1);

                User user2 = new User();
                user2.setId(uId2);
                user2.setElo(elo1);

                List<Round> rounds = new ArrayList<>();
                Round testRound = new Round();

                Scoreboard scoreboard = new Scoreboard();
                List<ScoreboardEntry> scoreboardEntrys = new ArrayList<>();
                ScoreboardEntry entry1 = new ScoreboardEntry(uId1, 0.0);
                ScoreboardEntry entry2 = new ScoreboardEntry(uId2, 0.0);
                scoreboardEntrys.add(entry1);
                scoreboardEntrys.add(entry2);
                scoreboard.setScoreboardEntries(scoreboardEntrys);
                testRound.setScoreboard(scoreboard);

                Match testMatch = new Match();
                testMatch.setRound(testRound);
                testMatch.setPlayer1(uId1);
                testMatch.setPlayer2(uId2);
                rounds.add(testRound);

                when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1))
                                .thenReturn(testMatch);
                when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId2, testRound, uId2))
                                .thenReturn(null);
                when(userRepository.findById(uId1)).thenReturn(Optional.of(user1));
                when(userRepository.findById(uId2)).thenReturn(Optional.of(user2));

                scoreboardComparator=new ScoreboardComparator(rounds, testRound, userRepository, matchRepository);
                Exception exception = assertThrows(Exception.class, () -> {
                        scoreboardComparator.ratingTiebreak(user1.getId(), user2.getId());
                });

                assertEquals("Match not found", exception.getMessage());

                verify(matchRepository).findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1);
                verify(matchRepository).findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId2, testRound, uId2);
                verify(userRepository).findById(uId1);
                verify(userRepository,times(2)).findById(uId2);
        }
}
