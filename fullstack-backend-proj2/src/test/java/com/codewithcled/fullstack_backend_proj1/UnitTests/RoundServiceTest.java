package com.codewithcled.fullstack_backend_proj1.UnitTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import com.codewithcled.fullstack_backend_proj1.model.Match;
import com.codewithcled.fullstack_backend_proj1.model.Round;
import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.repository.MatchRepository;
import com.codewithcled.fullstack_backend_proj1.repository.RoundRepository;
import com.codewithcled.fullstack_backend_proj1.repository.TournamentRepository;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;
import com.codewithcled.fullstack_backend_proj1.service.MatchService;
import com.codewithcled.fullstack_backend_proj1.service.RoundServiceImplementation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
public class RoundServiceTest {

    @Mock
    private RoundRepository roundRepository;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MatchService matchService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RoundServiceImplementation roundService;

    @Test
    void createFirstRound_Success() throws Exception {
        Long tId = (long) 132;
        Tournament testTournament = new Tournament();
        testTournament.setId(tId);
        Round testRound = new Round();
        User testUser1 = new User();
        testUser1.setId(tId + 1);
        testUser1.setElo((double) 13232);
        User testUser2 = new User();
        testUser2.setId(tId + 2);
        testUser2.setElo((double) 13252);
        List<User> participants = List.of(testUser1, testUser2);
        testTournament.setParticipants(participants);
        Match testMatch = new Match();
        testRound.setMatchList(List.of(testMatch));

        // Arrange
        when(tournamentRepository.findById(tId)).thenReturn(Optional.of(testTournament));
        when(matchService.createMatch(testUser1, testUser2)).thenReturn(new Match());
        when(roundRepository.save(any(Round.class))).thenAnswer(invocation -> {
            // Capture the round and return it (to avoid null pointer)
            return invocation.getArgument(0);
        });

        // Act
        Round round = roundService.createFirstRound(tId);

        // Assert
        assertNotNull(round);
        assertEquals(1, round.getRoundNum());
        assertEquals(testTournament, round.getTournament());
        assertEquals(1, round.getMatchList().size());
        assertEquals(0.0, round.getScoreboard().get(testUser1.getId()));
        assertEquals(0.0, round.getScoreboard().get(testUser2.getId()));

        // Verify interactions
        verify(tournamentRepository).findById(tId);
        verify(roundRepository).save(any(Round.class));
        verify(matchRepository).save(any(Match.class));

    }

    @Test
    void createFirstRound_Success_EloSwapped() throws Exception {
        Long tId = (long) 132;
        Tournament testTournament = new Tournament();
        testTournament.setId(tId);
        Round testRound = new Round();
        User testUser1 = new User();
        testUser1.setId(tId + 1);
        testUser1.setElo((double) 13252);
        User testUser2 = new User();
        testUser2.setId(tId + 2);
        testUser2.setElo((double) 13232);
        List<User> participants = List.of(testUser1, testUser2);
        testTournament.setParticipants(participants);
        Match testMatch = new Match();
        testRound.setMatchList(List.of(testMatch));

        // Arrange
        when(tournamentRepository.findById(tId)).thenReturn(Optional.of(testTournament));
        when(matchService.createMatch(testUser2, testUser1)).thenReturn(new Match());// Users swap
        when(roundRepository.save(any(Round.class))).thenAnswer(invocation -> {
            // Capture the round and return it (to avoid null pointer)
            return invocation.getArgument(0);
        });

        // Act
        Round round = roundService.createFirstRound(tId);

        // Assert
        assertNotNull(round);
        assertEquals(1, round.getRoundNum());
        assertEquals(testTournament, round.getTournament());
        assertEquals(1, round.getMatchList().size());
        assertEquals(0.0, round.getScoreboard().get(testUser1.getId()));
        assertEquals(0.0, round.getScoreboard().get(testUser2.getId()));

        // Verify interactions
        verify(tournamentRepository).findById(tId);
        verify(roundRepository).save(any(Round.class));
        verify(matchRepository).save(any(Match.class));

    }

    @Test
    void createFirstRound_Failure_TournamentNotFound() throws Exception {
        Long tId = (long) 132;

        when(tournamentRepository.findById(tId)).thenReturn(Optional.empty());
        boolean exceptionThrown = false;

        try {
            roundService.createFirstRound(tId);
        } catch (Exception e) {
            assertEquals("Tournament not found", e.getMessage());
            exceptionThrown = true;
        }

        assertEquals(true, exceptionThrown);
        verify(tournamentRepository).findById(tId);
    }

    @Test
    void createFirstRound_Failure_ParticipantsNotEven() throws Exception {
        Long tId = (long) 132;
        Tournament testTournament = new Tournament();
        testTournament.setId(tId);
        User testUser1 = new User();
        testUser1.setId(tId + 1);
        testUser1.setElo((double) 13232);
        List<User> participants = List.of(testUser1);
        testTournament.setParticipants(participants);

        when(tournamentRepository.findById(tId)).thenReturn(Optional.of(testTournament));

        boolean exceptionThrown = false;

        try {
            roundService.createFirstRound(tId);
        } catch (Exception e) {
            assertEquals("Number of participants must be even", e.getMessage());
            exceptionThrown = true;
        }

        assertEquals(true, exceptionThrown);
        verify(tournamentRepository).findById(tId);
    }

    @Test
    void checkComplete_Success_NotAllMatchComplete() throws Exception {
        Long rId = (long) 24234;
        Tournament testTournament = new Tournament();
        Round testRound = new Round();
        testRound.setTournament(testTournament);
        Match testMatch = new Match();
        testMatch.setIsComplete(false);
        testRound.setMatchList(List.of(testMatch));

        when(roundRepository.findById(rId)).thenReturn(Optional.of(testRound));

        roundService.checkComplete(rId);

        verify(roundRepository).findById(rId);
    }

    @Test
    void checkComplete_Success_AllMatchComplete_SortDesc() throws Exception {

        Long rId = (long) 24234;
        Tournament testTournament = new Tournament();
        Round testRound = new Round();
        Match testMatch = new Match();
        testMatch.setIsComplete(true);
        testRound.setMatchList(List.of(testMatch));
        testRound.setTournament(testTournament);
        User testUser1 = new User();
        testUser1.setId(rId + 1);
        testUser1.setElo((double) 13232);
        User testUser2 = new User();
        testUser2.setId(rId + 2);
        testUser2.setElo((double) 13232);
        List<User> participants = List.of(testUser1, testUser2);
        testTournament.setParticipants(participants);

        Map<Long, Double> scoreboard = new HashMap<Long, Double>();
        scoreboard.put(rId + 1, 2.0);
        scoreboard.put(rId + 2, 1.0);
        testRound.setScoreboard(scoreboard);

        when(roundRepository.findById(rId)).thenReturn(Optional.of(testRound));

        roundService.checkComplete(rId);

        List<Long> keyOrderList = new ArrayList<>(List.of(rId + 2, rId + 1));
        int count = 0;
        for (Map.Entry<Long, Double> entry : testRound.getScoreboard().entrySet()) {
            assertEquals(1.0 + count, entry.getValue());
            assertEquals(keyOrderList.get(count), entry.getKey());
            count++;
        }

        verify(roundRepository).findById(rId);
    }

    @Test
    void checkComplete_Success_AllMatchComplete_SortAsc() throws Exception {

        Long rId = (long) 24234;
        Tournament testTournament = new Tournament();
        Round testRound = new Round();
        Match testMatch = new Match();
        testMatch.setIsComplete(true);
        testRound.setMatchList(List.of(testMatch));
        testRound.setTournament(testTournament);
        User testUser1 = new User();
        testUser1.setId(rId + 1);
        testUser1.setElo((double) 13232);
        User testUser2 = new User();
        testUser2.setId(rId + 2);
        testUser2.setElo((double) 13232);
        List<User> participants = List.of(testUser1, testUser2);
        testTournament.setParticipants(participants);

        Map<Long, Double> scoreboard = new HashMap<Long, Double>();
        scoreboard.put(rId + 1, 1.0);
        scoreboard.put(rId + 2, 2.0);
        testRound.setScoreboard(scoreboard);

        when(roundRepository.findById(rId)).thenReturn(Optional.of(testRound));

        roundService.checkComplete(rId);

        List<Long> keyOrderList = new ArrayList<>(List.of(rId + 1, rId + 2));
        int count = 0;
        for (Map.Entry<Long, Double> entry : testRound.getScoreboard().entrySet()) {
            assertEquals(1.0 + count, entry.getValue());
            assertEquals(keyOrderList.get(count), entry.getKey());
            count++;
        }

        verify(roundRepository).findById(rId);
    }

    @Test
    void checkComplete_Success_AllMatchComplete_ScoreboardTieBreak() throws Exception {

        Long rId = (long) 24234;
        Tournament testTournament = new Tournament();
        Round testRound = new Round();
        Match testMatch = new Match();
        testMatch.setIsComplete(true);
        testRound.setMatchList(List.of(testMatch));
        testRound.setTournament(testTournament);

        User testUser1 = new User();
        testUser1.setId(rId + 1);
        testUser1.setElo((double) 13232);

        User testUser2 = new User();
        testUser2.setId(rId + 2);
        testUser2.setElo((double) 13232);

        List<User> participants = List.of(testUser1, testUser2);
        testTournament.setParticipants(participants);
        testMatch.setPlayer1(rId + 1);
        testMatch.setPlayer2(rId + 2);

        Map<Long, Double> scoreboard = new LinkedHashMap<Long, Double>();
        scoreboard.put(rId + 1, 2.0);
        scoreboard.put(rId + 2, 2.0);
        testRound.setScoreboard(scoreboard);

        when(roundRepository.findById(rId)).thenReturn(Optional.of(testRound));

        // when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, rId + 1, testRound, rId + 1))
        //         .thenReturn(testMatch);
        // when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, rId + 2, testRound, rId + 2))
        //         .thenReturn(testMatch);
        when(userRepository.findById(rId + 1)).thenReturn(Optional.of(testUser1));
        when(userRepository.findById(rId + 2)).thenReturn(Optional.of(testUser2));

        roundService.checkComplete(rId);

        List<Long> keyOrderList = new ArrayList<>(List.of(rId + 1, rId + 2));
        int count = 0;
        for (Map.Entry<Long, Double> entry : testRound.getScoreboard().entrySet()) {
            assertEquals(keyOrderList.get(count), entry.getKey());
            count++;
        }

        verify(roundRepository).findById(rId);
    }

    @Test
    void checkComplete_Failure() throws Exception {
        Long rId = (long) 24234;

        when(roundRepository.findById(rId)).thenReturn(Optional.empty());
        boolean exceptionThrown = false;

        try {
            roundService.checkComplete(rId);
        } catch (Exception e) {
            assertEquals("Round not found", e.getMessage());
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
        verify(roundRepository).findById(rId);
    }

    @Test
    void createNextRound_Success() throws Exception {
        Long tId = (long) 132;
        Tournament testTournament = new Tournament();
        testTournament.setId(tId);
        Round testRound = new Round();
        Round prevRound = new Round();
        User testUser1 = new User();
        testUser1.setId(tId + 1);
        testUser1.setElo((double) 13232);
        User testUser2 = new User();
        testUser2.setId(tId + 2);
        testUser2.setElo((double) 13232);
        List<User> participants = List.of(testUser1, testUser2);
        testTournament.setParticipants(participants);
        Match testMatch = new Match();
        testRound.setMatchList(List.of(testMatch));
        Map<Long, Double> scoreboard = new HashMap<Long, Double>();
        scoreboard.put(tId + 1, 0.0);
        scoreboard.put(tId + 2, 0.0);
        prevRound.setScoreboard(scoreboard);
        List<Round> roundList = new ArrayList<>();
        roundList.add(prevRound);
        testTournament.setRounds(roundList);

        when(tournamentRepository.findById(tId)).thenReturn(Optional.of(testTournament));
        when(userRepository.findById(tId + 1)).thenReturn(Optional.of(testUser1));
        when(userRepository.findById(tId + 2)).thenReturn(Optional.of(testUser2));
        when(matchService.createMatch(testUser1, testUser2)).thenReturn(testMatch);
        // return newRound when save
        when(roundRepository.save(any(Round.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Round result = roundService.createNextRound(tId);

        assertEquals(2, result.getRoundNum());
        assertEquals(testTournament, result.getTournament());
        assertIterableEquals(List.of(testMatch), result.getMatchList());

        verify(tournamentRepository).findById(tId);
        verify(userRepository).findById(tId + 1);
        verify(userRepository).findById(tId + 2);
        verify(matchService).createMatch(testUser1, testUser2);
        verify(roundRepository).save(any(Round.class));
    }

    // Sorting scoreboard moved to checkCOmplete
    // @Test
    // void createNextRound_Success_ScoreboardSortDesc() throws Exception {
    // Long tId = (long) 132;
    // Tournament testTournament = new Tournament();
    // testTournament.setId(tId);
    // Round testRound = new Round();
    // Round prevRound = new Round();
    // User testUser1 = new User();
    // testUser1.setId(tId + 1);
    // testUser1.setElo((double) 13232);
    // User testUser2 = new User();
    // testUser2.setId(tId + 2);
    // testUser2.setElo((double) 13232);
    // List<User> participants = List.of(testUser1, testUser2);
    // testTournament.setParticipants(participants);
    // Match testMatch = new Match();
    // testRound.setMatchList(List.of(testMatch));
    // Map<Long, Double> scoreboard = new HashMap<Long, Double>();
    // scoreboard.put(tId + 1, 1.0);
    // scoreboard.put(tId + 2, 2.0);
    // prevRound.setScoreboard(scoreboard);
    // List<Round> roundList=new ArrayList<>();
    // roundList.add(prevRound);
    // testTournament.setRounds(roundList);

    // when(tournamentRepository.findById(tId)).thenReturn(Optional.of(testTournament));
    // when(userRepository.findById(tId + 1)).thenReturn(Optional.of(testUser1));
    // when(userRepository.findById(tId + 2)).thenReturn(Optional.of(testUser2));
    // when(matchService.createMatch(testUser2, testUser1)).thenReturn(testMatch);
    // // return newRound when save
    // when(roundRepository.save(any(Round.class))).thenAnswer(invocation ->
    // invocation.getArgument(0));

    // Round result = roundService.createNextRound(tId);

    // assertEquals(2, result.getRoundNum());
    // assertEquals(testTournament, result.getTournament());
    // assertIterableEquals(List.of(testMatch), result.getMatchList());

    // verify(tournamentRepository).findById(tId);
    // verify(userRepository).findById(tId + 1);
    // verify(userRepository).findById(tId + 2);
    // verify(matchService).createMatch(testUser2, testUser1);
    // verify(roundRepository).save(any(Round.class));
    // }

    // @Test
    // void createNextRound_Success_ScoreboardSort2Asc() throws Exception {
    // Long tId = (long) 132;
    // Tournament testTournament = new Tournament();
    // testTournament.setId(tId);
    // Round testRound = new Round();
    // Round prevRound = new Round();
    // User testUser1 = new User();
    // testUser1.setId(tId + 1);
    // testUser1.setElo((double) 13232);
    // User testUser2 = new User();
    // testUser2.setId(tId + 2);
    // testUser2.setElo((double) 13232);
    // List<User> participants = List.of(testUser1, testUser2);
    // testTournament.setParticipants(participants);
    // Match testMatch = new Match();
    // testRound.setMatchList(List.of(testMatch));
    // Map<Long, Double> scoreboard = new HashMap<Long, Double>();
    // scoreboard.put(tId + 1, 2.0);
    // scoreboard.put(tId + 2, 1.0);
    // prevRound.setScoreboard(scoreboard);
    // List<Round> roundList=new ArrayList<>();
    // roundList.add(prevRound);
    // testTournament.setRounds(roundList);

    // when(tournamentRepository.findById(tId)).thenReturn(Optional.of(testTournament));
    // when(userRepository.findById(tId + 1)).thenReturn(Optional.of(testUser1));
    // when(userRepository.findById(tId + 2)).thenReturn(Optional.of(testUser2));
    // when(matchService.createMatch(testUser1,
    // testUser2)).thenReturn(testMatch);//Swapped Users
    // // return newRound when save
    // when(roundRepository.save(any(Round.class))).thenAnswer(invocation ->
    // invocation.getArgument(0));

    // Round result = roundService.createNextRound(tId);

    // assertEquals(2, result.getRoundNum());
    // assertEquals(testTournament, result.getTournament());
    // assertIterableEquals(List.of(testMatch), result.getMatchList());

    // verify(tournamentRepository).findById(tId);
    // verify(userRepository).findById(tId + 1);
    // verify(userRepository).findById(tId + 2);
    // verify(matchService).createMatch(testUser1, testUser2);
    // verify(roundRepository).save(any(Round.class));
    // }

    @Test
    void createNextRound_Failure_TournamentNotFound() throws Exception {
        Long tId = (long) 132;

        when(tournamentRepository.findById(tId)).thenReturn(Optional.empty());

        boolean exceptionThrown = false;
        try {
            roundService.createNextRound(tId);
        } catch (Exception e) {
            assertEquals("Tournament not found", e.getMessage());
            exceptionThrown = true;
        }

        assertEquals(true, exceptionThrown);
        verify(tournamentRepository).findById(tId);
    }

    @Test
    void createNextRound_Failure_Player1NotFound() throws Exception {
        Long tId = (long) 132;
        Tournament testTournament = new Tournament();
        testTournament.setId(tId);
        Round testRound = new Round();
        Round prevRound = new Round();
        User testUser1 = new User();
        testUser1.setId(tId + 1);
        testUser1.setElo((double) 13232);
        User testUser2 = new User();
        testUser2.setId(tId + 2);
        testUser2.setElo((double) 13232);
        List<User> participants = List.of(testUser1, testUser2);
        testTournament.setParticipants(participants);
        Match testMatch = new Match();
        testRound.setMatchList(List.of(testMatch));
        Map<Long, Double> scoreboard = new HashMap<Long, Double>();
        scoreboard.put(tId + 1, 0.0);
        scoreboard.put(tId + 2, 0.0);
        prevRound.setScoreboard(scoreboard);
        List<Round> roundList = new ArrayList<>();
        roundList.add(prevRound);
        testTournament.setRounds(roundList);

        when(tournamentRepository.findById(tId)).thenReturn(Optional.of(testTournament));
        when(userRepository.findById(tId + 1)).thenReturn(Optional.empty());

        boolean exceptionThrown = false;
        try {
            roundService.createNextRound(tId);
        } catch (Exception e) {
            assertEquals("User not found", e.getMessage());
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);

        verify(tournamentRepository).findById(tId);
        verify(userRepository).findById(tId + 1);
    }

    @Test
    void createNextRound_Failure_Player2NotFound() throws Exception {
        Long tId = (long) 132;
        Tournament testTournament = new Tournament();
        testTournament.setId(tId);
        Round testRound = new Round();
        Round prevRound = new Round();
        User testUser1 = new User();
        testUser1.setId(tId + 1);
        testUser1.setElo((double) 13232);
        User testUser2 = new User();
        testUser2.setId(tId + 2);
        testUser2.setElo((double) 13232);
        List<User> participants = List.of(testUser1, testUser2);
        testTournament.setParticipants(participants);
        Match testMatch = new Match();
        testRound.setMatchList(List.of(testMatch));
        Map<Long, Double> scoreboard = new HashMap<Long, Double>();
        scoreboard.put(tId + 1, 0.0);
        scoreboard.put(tId + 2, 0.0);
        prevRound.setScoreboard(scoreboard);
        List<Round> roundList = new ArrayList<>();
        roundList.add(prevRound);
        testTournament.setRounds(roundList);

        when(tournamentRepository.findById(tId)).thenReturn(Optional.of(testTournament));
        when(userRepository.findById(tId + 1)).thenReturn(Optional.of(testUser1));
        when(userRepository.findById(tId + 2)).thenReturn(Optional.empty());

        boolean exceptionThrown = false;
        try {
            roundService.createNextRound(tId);
        } catch (Exception e) {
            assertEquals("User not found", e.getMessage());
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);

        verify(tournamentRepository).findById(tId);
        verify(userRepository).findById(tId + 1);
        verify(userRepository).findById(tId + 2);
    }

    @Test
    void getAllMatches_Success() throws Exception {
        Match testMatch1 = new Match();
        testMatch1.setId((long) 1);
        Match testMatch2 = new Match();
        testMatch2.setId((long) 2);
        List<Match> matchList = new ArrayList<>();
        matchList.add(testMatch1);
        matchList.add(testMatch2);

        Long rId = (long) 132;
        Round testRound = new Round();
        testRound.setId(rId);
        testRound.setMatchList(matchList);

        when(roundRepository.findById(rId)).thenReturn(Optional.of(testRound));

        List<Match> result = roundService.getAllMatches(rId);

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(2, result.get(1).getId());

        verify(roundRepository).findById(rId);
    }

    @Test
    void getAllMatches_Failure_RoundNotFound() {
        Match testMatch1 = new Match();
        testMatch1.setId((long) 1);
        Match testMatch2 = new Match();
        testMatch2.setId((long) 2);
        List<Match> matchList = new ArrayList<>();
        matchList.add(testMatch1);
        matchList.add(testMatch2);

        Long rId = (long) 132;

        when(roundRepository.findById(rId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            roundService.getAllMatches(rId);
        });

        assertEquals("Round not found", exception.getMessage());
        verify(roundRepository).findById(rId);
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

        Map<Long, Double> scoreboard = new HashMap<Long, Double>();
        scoreboard.put(uId1, 0.0);// Means that player 1 and player 2 played against each other.
        scoreboard.put(uId2, 1.0);// Since player 1 beat player 2, player1 is tougher opponent
                                  // If score same at the end the player who fought tougher opponents wins
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

        int result = roundService.solkoffTiebreak(user1, user2, rounds, testRound);

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

        Map<Long, Double> scoreboard = new HashMap<Long, Double>();
        scoreboard.put(uId1, 1.0);
        scoreboard.put(uId2, 0.0);
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

        int result = roundService.solkoffTiebreak(user1, user2, rounds, testRound);

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

        Map<Long, Double> scoreboard = new HashMap<Long, Double>();
        scoreboard.put(uId1, 0.0);
        scoreboard.put(uId2, 0.0);
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

        int result = roundService.solkoffTiebreak(user1, user2, rounds, testRound);

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

        Map<Long, Double> scoreboard = new HashMap<Long, Double>();
        scoreboard.put(uId1, 0.0);
        scoreboard.put(uId2, 0.0);
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

        int result = roundService.solkoffTiebreak(user1, user2, rounds, testRound);

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

        Map<Long, Double> scoreboard = new HashMap<Long, Double>();
        scoreboard.put(uId1, 0.0);
        scoreboard.put(uId2, 0.0);
        testRound.setScoreboard(scoreboard);

        Match testMatch = new Match();
        testMatch.setRound(testRound);
        testMatch.setPlayer1(uId1);
        testMatch.setPlayer2(uId2);
        rounds.add(testRound);

        when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1)).thenReturn(null);

        Exception exception = assertThrows(Exception.class, () -> {
            roundService.solkoffTiebreak(user1, user2, rounds, testRound);
        });

        assertEquals("Match not found", exception.getMessage());
        verify(matchRepository).findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1);
        verify(matchRepository, never()).findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId2, testRound, uId2);
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

        Map<Long, Double> scoreboard = new HashMap<Long, Double>();
        scoreboard.put(uId1, 0.0);
        scoreboard.put(uId2, 0.0);
        testRound.setScoreboard(scoreboard);

        Match testMatch = new Match();
        testMatch.setRound(testRound);
        testMatch.setPlayer1(uId1);
        testMatch.setPlayer2(uId2);
        rounds.add(testRound);

        when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1))
                .thenReturn(testMatch);
        when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId2, testRound, uId2)).thenReturn(null);

        Exception exception = assertThrows(Exception.class, () -> {
            roundService.solkoffTiebreak(user1, user2, rounds, testRound);
        });

        assertEquals("Match not found", exception.getMessage());
        verify(matchRepository).findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1);
        verify(matchRepository).findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId2, testRound, uId2);
    }

    @Test
    void ratingTiebreak_Success_Player1OppEloGreater_Return1() throws Exception {
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
        when(userRepository.findById(uId3)).thenReturn(Optional.of(opp1));
        when(userRepository.findById(uId4)).thenReturn(Optional.of(opp2));

        int result = roundService.ratingTiebreak(user1, user2, rounds, testRound);

        assertEquals(1, result);
        verify(matchRepository).findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1);
        verify(matchRepository).findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId2, testRound, uId2);
        verify(userRepository).findById(uId3);
        verify(userRepository).findById(uId4);
    }

    @Test
    void ratingTiebreak_Success_Player1OppEloLesser_ReturnNeg1() throws Exception {
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
        when(userRepository.findById(uId3)).thenReturn(Optional.of(opp1));
        when(userRepository.findById(uId4)).thenReturn(Optional.of(opp2));

        int result = roundService.ratingTiebreak(user1, user2, rounds, testRound);

        assertEquals(-1, result);

        verify(matchRepository).findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1);
        verify(matchRepository).findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId2, testRound, uId2);
        verify(userRepository).findById(uId3);
        verify(userRepository).findById(uId4);
    }

    @Test
    void ratingTiebreak_Success_Player1OppEloLesser_WorksWhenSwappingOpponents_ReturnNeg1() throws Exception {
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
        when(userRepository.findById(uId3)).thenReturn(Optional.of(opp1));
        when(userRepository.findById(uId4)).thenReturn(Optional.of(opp2));

        int result = roundService.ratingTiebreak(user1, user2, rounds, testRound);

        assertEquals(-1, result);

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

        Map<Long, Double> scoreboard = new HashMap<Long, Double>();
        scoreboard.put(uId1, 0.0);
        scoreboard.put(uId2, 0.0);
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

        int result = roundService.ratingTiebreak(user1, user2, rounds, testRound);

        assertEquals(0, result);
        verify(matchRepository).findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1);
        verify(matchRepository).findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId2, testRound, uId2);
        verify(userRepository).findById(uId1);
        verify(userRepository).findById(uId2);
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

        Map<Long, Double> scoreboard = new HashMap<Long, Double>();
        scoreboard.put(uId1, 0.0);
        scoreboard.put(uId2, 0.0);
        testRound.setScoreboard(scoreboard);

        Match testMatch = new Match();
        testMatch.setRound(testRound);
        testMatch.setPlayer1(uId1);
        testMatch.setPlayer2(uId2);
        rounds.add(testRound);

        when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1))
                .thenReturn(null);

        Exception exception = assertThrows(Exception.class, () -> {
            roundService.ratingTiebreak(user1, user2, rounds, testRound);
        });

        assertEquals("Match not found", exception.getMessage());
        verify(matchRepository).findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1);
        verify(matchRepository, never()).findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId2, testRound, uId2);
        verify(userRepository, never()).findById(uId1);
        verify(userRepository, never()).findById(uId2);
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

        Map<Long, Double> scoreboard = new HashMap<Long, Double>();
        scoreboard.put(uId1, 0.0);
        scoreboard.put(uId2, 0.0);
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
        when(userRepository.findById(uId2)).thenReturn(Optional.of(user2));

        Exception exception = assertThrows(Exception.class, () -> {
            roundService.ratingTiebreak(user1, user2, rounds, testRound);
        });

        assertEquals("Match not found", exception.getMessage());

        verify(matchRepository).findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1);
        verify(matchRepository).findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId2, testRound, uId2);
        verify(userRepository, never()).findById(uId1);
        verify(userRepository).findById(uId2);
    }

}
