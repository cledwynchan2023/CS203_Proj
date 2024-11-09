package com.codewithcled.fullstack_backend_proj1.UnitTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import com.codewithcled.fullstack_backend_proj1.model.Match;
import com.codewithcled.fullstack_backend_proj1.model.Round;
import com.codewithcled.fullstack_backend_proj1.model.Scoreboard;
import com.codewithcled.fullstack_backend_proj1.model.ScoreboardEntry;
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
    void createFirstRound_Success_ReturnCreatedRound() throws Exception {
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
        assertEquals(0.0, round.getScoreboard().getPlayerScore(testUser1.getId()));
        assertEquals(0.0, round.getScoreboard().getPlayerScore(testUser2.getId()));

        // Verify interactions
        verify(tournamentRepository).findById(tId);
        verify(roundRepository).save(any(Round.class));
        verify(matchRepository).save(any(Match.class));

    }

    @Test
    void createFirstRound_Success_EloSwapped_ReturnCreatedRound() throws Exception {
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
        assertEquals(0.0, round.getScoreboard().getPlayerScore(testUser1.getId()));
        assertEquals(0.0, round.getScoreboard().getPlayerScore(testUser2.getId()));

        // Verify interactions
        verify(tournamentRepository).findById(tId);
        verify(roundRepository).save(any(Round.class));
        verify(matchRepository).save(any(Match.class));

    }

    @Test
    void createFirstRound_Failure_TournamentNotFound_ReturnException() throws Exception {
        Long tId = (long) 132;

        // Arrange
        when(tournamentRepository.findById(tId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            roundService.createFirstRound(tId);
        });

        // Assert
        assertEquals("Tournament not found", exception.getMessage());
        verify(tournamentRepository).findById(tId);
    }

    @Test
    void createFirstRound_Failure_ParticipantsNotEven_ReturnsException() throws Exception {
        // arrange
        Long tId = (long) 132;
        Tournament testTournament = new Tournament();
        testTournament.setId(tId);
        User testUser1 = new User();
        testUser1.setId(tId + 1);
        testUser1.setElo((double) 13232);
        List<User> participants = List.of(testUser1);
        testTournament.setParticipants(participants);

        when(tournamentRepository.findById(tId)).thenReturn(Optional.of(testTournament));

        // act
        Exception exception = assertThrows(Exception.class, () -> {
            roundService.createFirstRound(tId);
        });

        // assert
        assertEquals("Number of participants must be even", exception.getMessage());
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
    void checkComplete_Success_AllMatchComplete_ReturnRoundUpdate() throws Exception {

        Long rId = (long) 24234;
        Tournament testTournament = new Tournament();
        Round testRound = new Round();
        Match testMatch = new Match();
        testMatch.setIsComplete(true);
        testRound.setMatchList(List.of(testMatch));
        testRound.setTournament(testTournament);
        testRound.setIsCompleted(false);
        User testUser1 = new User();
        testUser1.setId(rId + 1);
        testUser1.setElo((double) 13232);
        User testUser2 = new User();
        testUser2.setId(rId + 2);
        testUser2.setElo((double) 13232);
        List<User> participants = List.of(testUser1, testUser2);
        testTournament.setParticipants(participants);

        Scoreboard scoreboard = new Scoreboard();
        List<ScoreboardEntry> scoreboardEntrys = new ArrayList<>();
        ScoreboardEntry entry1 = new ScoreboardEntry(rId + 1, 2.0);
        ScoreboardEntry entry2 = new ScoreboardEntry(rId + 2, 1.0);
        scoreboardEntrys.add(entry1);
        scoreboardEntrys.add(entry2);
        scoreboard.setScoreboardEntries(scoreboardEntrys);
        testRound.setScoreboard(scoreboard);

        when(roundRepository.findById(rId)).thenReturn(Optional.of(testRound));

        roundService.checkComplete(rId);

        assertTrue(testRound.getIsCompleted());

        List<Long> keyOrderList = new ArrayList<>(List.of(rId + 1, rId + 2));
        List<ScoreboardEntry> entries = testRound.getScoreboard().getScoreboardEntries();
        int count = 0;
        for (ScoreboardEntry entry : entries) {
            assertEquals(2.0 - count, entry.getScore());
            assertEquals(keyOrderList.get(count), entry.getPlayerId());
            count++;
        }

        verify(roundRepository).findById(rId);
    }

    @Test
    void checkComplete_Failure_RoundNotFound_ReturnException() throws Exception {
        Long rId = (long) 24234;

        when(roundRepository.findById(rId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            roundService.checkComplete(rId);
        });
        assertEquals("Round not found", exception.getMessage());
        verify(roundRepository).findById(rId);
    }

    @Test
    void createNextRound_Success_ReturnNewRound() throws Exception {
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

        Scoreboard scoreboard = new Scoreboard();
        List<ScoreboardEntry> scoreboardEntrys = new ArrayList<>();
        ScoreboardEntry entry1 = new ScoreboardEntry(tId + 1, 0.0);
        ScoreboardEntry entry2 = new ScoreboardEntry(tId + 2, 0.0);
        scoreboardEntrys.add(entry1);
        scoreboardEntrys.add(entry2);
        scoreboard.setScoreboardEntries(scoreboardEntrys);

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
        assertNotNull(result.getScoreboard());
        List<ScoreboardEntry> scoreboardEntries = result.getScoreboard().getScoreboardEntries();
        int count = 1;
        for (ScoreboardEntry entry : scoreboardEntries) {
            assertEquals(tId + count, entry.getPlayerId());
            count++;
        }

        verify(tournamentRepository).findById(tId);
        verify(userRepository).findById(tId + 1);
        verify(userRepository).findById(tId + 2);
        verify(matchService).createMatch(testUser1, testUser2);
        verify(roundRepository).save(any(Round.class));
    }

    @Test
    void createNextRound_Failure_TournamentNotFound_ReturnException() throws Exception {
        Long tId = (long) 132;

        when(tournamentRepository.findById(tId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            roundService.createNextRound(tId);
        });
        assertEquals("Tournament not found", exception.getMessage());

        verify(tournamentRepository).findById(tId);
    }

    @Test
    void createNextRound_Failure_Player1NotFound_ReturnException() throws Exception {
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

        Scoreboard scoreboard = new Scoreboard();
        List<ScoreboardEntry> scoreboardEntrys = new ArrayList<>();
        ScoreboardEntry entry1 = new ScoreboardEntry(tId + 1, 0.0);
        ScoreboardEntry entry2 = new ScoreboardEntry(tId + 2, 0.0);
        scoreboardEntrys.add(entry1);
        scoreboardEntrys.add(entry2);
        scoreboard.setScoreboardEntries(scoreboardEntrys);

        prevRound.setScoreboard(scoreboard);
        List<Round> roundList = new ArrayList<>();
        roundList.add(prevRound);
        testTournament.setRounds(roundList);

        when(tournamentRepository.findById(tId)).thenReturn(Optional.of(testTournament));
        when(userRepository.findById(tId + 1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            roundService.createNextRound(tId);
        });
        assertEquals("User not found", exception.getMessage());

        verify(tournamentRepository).findById(tId);
        verify(userRepository).findById(tId + 1);
    }

    @Test
    void createNextRound_Failure_Player2NotFound_ReturnException() throws Exception {
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

        Scoreboard scoreboard = new Scoreboard();
        List<ScoreboardEntry> scoreboardEntrys = new ArrayList<>();
        ScoreboardEntry entry1 = new ScoreboardEntry(tId + 1, 0.0);
        ScoreboardEntry entry2 = new ScoreboardEntry(tId + 2, 0.0);
        scoreboardEntrys.add(entry1);
        scoreboardEntrys.add(entry2);
        scoreboard.setScoreboardEntries(scoreboardEntrys);

        prevRound.setScoreboard(scoreboard);
        List<Round> roundList = new ArrayList<>();
        roundList.add(prevRound);
        testTournament.setRounds(roundList);

        when(tournamentRepository.findById(tId)).thenReturn(Optional.of(testTournament));
        when(userRepository.findById(tId + 1)).thenReturn(Optional.of(testUser1));
        when(userRepository.findById(tId + 2)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            roundService.createNextRound(tId);
        });
        assertEquals("User not found", exception.getMessage());

        verify(tournamentRepository).findById(tId);
        verify(userRepository).findById(tId + 1);
        verify(userRepository).findById(tId + 2);
    }

    @Test
    void getAllMatches_Success_ReturnMatchList() throws Exception {
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
    void getAllMatches_Failure_RoundNotFound_ReturnException() {
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

}
