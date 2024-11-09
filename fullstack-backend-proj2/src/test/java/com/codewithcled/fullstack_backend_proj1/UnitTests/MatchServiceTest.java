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
import com.codewithcled.fullstack_backend_proj1.service.EloRatingService;
import com.codewithcled.fullstack_backend_proj1.service.MatchServiceImplementation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private RoundRepository roundRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EloRatingService eloRatingService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private MatchServiceImplementation matchService;

    @Test
    void createMatch_Success_ReturnsCorrectMatch() {
        // Arrange
        Double elo1 = 1000.0;
        Double elo2 = 1010.0;
        long uId1 = 1L;
        long uId2 = 2L;

        User p1 = new User();
        p1.setElo(elo1);
        p1.setId(uId1);

        User p2 = new User();
        p2.setElo(elo2);
        p2.setId(uId2);

        // Act
        Match result = matchService.createMatch(p1, p2);

        // Assert
        assertEquals(uId1, result.getPlayer1());
        assertEquals(uId2, result.getPlayer2());
        assertEquals(elo1, result.getPlayer1StartingElo());
        assertEquals(elo2, result.getPlayer2StartingElo());
    }

    @Test
    void updateRoundScoreboard_Success_Draw_ReturnsCorrectScores() throws Exception {
        // Arrange
        Round testRound = new Round();
        Match testMatch = new Match();
        Long uId1 = 1L;
        Long uId2 = 2L;
        User testPlayer1 = new User();
        User testPlayer2 = new User();
        testPlayer1.setId(uId1);
        testPlayer1.setElo(100.0);
        testPlayer2.setId(uId2);
        testPlayer2.setElo(200.0);
        testMatch.setPlayer1(uId1);
        testMatch.setPlayer2(uId2);

        Scoreboard scoreboard = new Scoreboard();
        List<ScoreboardEntry> scoreboardEntrys = new ArrayList<>();
        ScoreboardEntry entry1 = new ScoreboardEntry(uId1, 0.0);
        ScoreboardEntry entry2 = new ScoreboardEntry(uId2, 0.0);
        scoreboardEntrys.add(entry1);
        scoreboardEntrys.add(entry2);
        scoreboard.setScoreboardEntries(scoreboardEntrys);

        testRound.setScoreboard(scoreboard);
        Tournament tournament = new Tournament();
        testRound.setTournament(tournament);
        tournament.setRounds(List.of(testRound));

        when(userRepository.findById(uId1)).thenReturn(Optional.of(testPlayer1));
        when(userRepository.findById(uId2)).thenReturn(Optional.of(testPlayer2));
        when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1))
                .thenReturn(testMatch);
        when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId2, testRound, uId2))
                .thenReturn(testMatch);

        // Act
        matchService.updateRoundScoreboard(testRound, testMatch, 0);

        // Assert
        assertEquals(0.5, scoreboard.getPlayerScore(uId1));
        assertEquals(0.5, scoreboard.getPlayerScore(uId2));
        verify(roundRepository).save(testRound);
    }

    @Test
    void updateRoundScoreboard_Success_Draw_SortBySolkoff() throws Exception {
        // Arrange
        Round testRound = new Round();
        Match testMatch = new Match();
        Match testMatch2 = new Match();
        Long uId1 = 1L;
        Long uId2 = 2L;
        Long uId3 = 3L;
        Long uId4 = 4L;
        User testPlayer1 = new User();
        User testPlayer2 = new User();
        User opp1 = new User();
        User opp2 = new User();

        testPlayer1.setId(uId1);
        testPlayer2.setId(uId2);
        opp1.setId(uId3);
        opp2.setId(uId4);
        testMatch.setPlayer1(uId1);
        testMatch.setPlayer2(uId2);
        testMatch2.setPlayer1(uId3);
        testMatch2.setPlayer2(uId1);

        Scoreboard scoreboard = new Scoreboard();
        List<ScoreboardEntry> scoreboardEntrys = new ArrayList<>();
        ScoreboardEntry entry1 = new ScoreboardEntry(uId1, 1.0);
        ScoreboardEntry entry2 = new ScoreboardEntry(uId2, 1.0);
        ScoreboardEntry entry3 = new ScoreboardEntry(uId3, 2.0);
        ScoreboardEntry entry4 = new ScoreboardEntry(uId4, 2.0);
        scoreboardEntrys.add(entry1);
        scoreboardEntrys.add(entry2);
        scoreboardEntrys.add(entry3);
        scoreboardEntrys.add(entry4);
        scoreboard.setScoreboardEntries(scoreboardEntrys);

        testRound.setScoreboard(scoreboard);
        Tournament tournament = new Tournament();
        testRound.setTournament(tournament);
        tournament.setRounds(List.of(testRound));

        when(userRepository.findById(uId1)).thenReturn(Optional.of(testPlayer1));
        when(userRepository.findById(uId2)).thenReturn(Optional.of(testPlayer2));
        when(userRepository.findById(uId3)).thenReturn(Optional.of(opp1));
        when(userRepository.findById(uId4)).thenReturn(Optional.of(opp2));
        when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1))
                .thenReturn(testMatch2);
        when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId2, testRound, uId2))
                .thenReturn(testMatch);
        when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId3, testRound, uId3))
                .thenReturn(testMatch2);
        when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId4, testRound, uId4))
                .thenReturn(testMatch);

        // Act
        matchService.updateRoundScoreboard(testRound, testMatch, 0);

        // Assert
        assertEquals(1.5, scoreboard.getPlayerScore(uId1));
        assertEquals(1.5, scoreboard.getPlayerScore(uId2));
        List<Long> keyOrder = List.of(uId2, uId1, uId3, uId4);
        int count = 0;
        for (ScoreboardEntry entry : scoreboard.getScoreboardEntries()) {
            assertEquals(keyOrder.get(count), entry.getPlayerId());
            count++;
        }
        verify(roundRepository).save(testRound);
    }

    @Test
    void updateRoundScoreboard_Success_Draw_SortByElo() throws Exception {
        // Arrange
        Round testRound = new Round();
        Match testMatch = new Match();
        Match testMatch2 = new Match();
        Long uId1 = 1L;
        Long uId2 = 2L;
        Long uId3 = 3L;
        Long uId4 = 4L;
        Double elo1 = 100.0;
        Double elo2 = elo1 + 100;
        Double elo3 = elo1 + 200;
        User testPlayer1 = new User();
        User testPlayer2 = new User();
        User opp1 = new User();
        User opp2 = new User();

        testPlayer1.setId(uId1);
        testPlayer1.setElo(elo1);
        testPlayer2.setId(uId2);
        testPlayer2.setElo(elo1);
        opp1.setId(uId3);
        opp1.setElo(elo2);
        opp2.setId(uId4);
        opp2.setElo(elo3);
        testMatch.setPlayer1(uId1);
        testMatch.setPlayer2(uId2);
        testMatch2.setPlayer1(uId3);
        testMatch2.setPlayer2(uId2);

        Scoreboard scoreboard = new Scoreboard();
        List<ScoreboardEntry> scoreboardEntrys = new ArrayList<>();
        ScoreboardEntry entry1 = new ScoreboardEntry(uId1, 0.0);
        ScoreboardEntry entry2 = new ScoreboardEntry(uId2, 0.0);
        ScoreboardEntry entry3 = new ScoreboardEntry(uId3, 0.5);
        ScoreboardEntry entry4 = new ScoreboardEntry(uId4, 0.5);
        scoreboardEntrys.add(entry1);
        scoreboardEntrys.add(entry2);
        scoreboardEntrys.add(entry3);
        scoreboardEntrys.add(entry4);
        scoreboard.setScoreboardEntries(scoreboardEntrys);

        testRound.setScoreboard(scoreboard);
        Tournament tournament = new Tournament();
        testRound.setTournament(tournament);
        tournament.setRounds(List.of(testRound));

        when(userRepository.findById(uId1)).thenReturn(Optional.of(testPlayer1));
        when(userRepository.findById(uId2)).thenReturn(Optional.of(testPlayer2));
        when(userRepository.findById(uId3)).thenReturn(Optional.of(opp1));
        when(userRepository.findById(uId4)).thenReturn(Optional.of(opp2));
        when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1))
                .thenReturn(testMatch2);
        when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId2, testRound, uId2))
                .thenReturn(testMatch);
        when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId3, testRound, uId3))
                .thenReturn(testMatch2);
        when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId4, testRound, uId4))
                .thenReturn(testMatch);

        // Act
        matchService.updateRoundScoreboard(testRound, testMatch, 0);

        // Assert
        assertEquals(0.5, scoreboard.getPlayerScore(uId1));
        assertEquals(0.5, scoreboard.getPlayerScore(uId2));
        assertEquals(0.5, scoreboard.getPlayerScore(uId3));
        assertEquals(0.5, scoreboard.getPlayerScore(uId4));

        List<Long> keyOrder = List.of(uId2, uId1, uId3, uId4);
        int count = 0;
        for (ScoreboardEntry entry : scoreboard.getScoreboardEntries()) {
            assertEquals(keyOrder.get(count), entry.getPlayerId());
            count++;
        }
        verify(roundRepository).save(testRound);
    }

    @Test
    void updateRoundScoreboard_Player1Win_Success_UpdatedScores() throws Exception {
        // Arrange
        Round testRound = new Round();
        Match testMatch = new Match();
        Long uId1 = (long) 1;
        Long uId2 = uId1 + 1;
        testMatch.setPlayer1(uId1);
        testMatch.setPlayer2(uId2);
    
        Scoreboard scoreboard = new Scoreboard();
        List<ScoreboardEntry> scoreboardEntrys = new ArrayList<>();
        ScoreboardEntry entry1 = new ScoreboardEntry(uId1, 0.0);
        ScoreboardEntry entry2 = new ScoreboardEntry(uId2, 0.0);
        scoreboardEntrys.add(entry1);
        scoreboardEntrys.add(entry2);
        scoreboard.setScoreboardEntries(scoreboardEntrys);
    
        testRound.setScoreboard(scoreboard);
        Tournament tournament = new Tournament();
        testRound.setTournament(tournament);
        tournament.setRounds(List.of(testRound));
    
        // Act
        matchService.updateRoundScoreboard(testRound, testMatch, -1);
    
        // Assert
        assertEquals(1.0, scoreboard.getPlayerScore(uId1));
        assertEquals(0.0, scoreboard.getPlayerScore(uId2));
        verify(roundRepository).save(testRound);
    }
    
    @Test
    void updateRoundScoreboard_Player2Win_Success_UpdatedScores() throws Exception {
        // Arrange
        Round testRound = new Round();
        Match testMatch = new Match();
        Long uId1 = (long) 1;
        Long uId2 = uId1 + 1;
        testMatch.setPlayer1(uId1);
        testMatch.setPlayer2(uId2);
    
        Scoreboard scoreboard = new Scoreboard();
        List<ScoreboardEntry> scoreboardEntrys = new ArrayList<>();
        ScoreboardEntry entry1 = new ScoreboardEntry(uId1, 0.0);
        ScoreboardEntry entry2 = new ScoreboardEntry(uId2, 0.0);
        scoreboardEntrys.add(entry1);
        scoreboardEntrys.add(entry2);
        scoreboard.setScoreboardEntries(scoreboardEntrys);
    
        testRound.setScoreboard(scoreboard);
    
        Tournament tournament = new Tournament();
        testRound.setTournament(tournament);
        tournament.setRounds(List.of(testRound));
    
        // Act
        matchService.updateRoundScoreboard(testRound, testMatch, 1);
    
        // Assert
        assertEquals(1.0, scoreboard.getPlayerScore(uId2));
        assertEquals(0.0, scoreboard.getPlayerScore(uId1)); // Player 1 should not have scored
        verify(roundRepository).save(testRound);
    }
    
    @Test
    void updateRoundScoreboard_Player1MissingFromScoreboard_Failure_PlayerNotFoundException() throws Exception {
        // Arrange
        Round testRound = new Round();
        Match testMatch = new Match();
        Long uId1 = (long) 1;
        Long uId2 = uId1 + 1;
        testMatch.setPlayer1(uId1);
        testMatch.setPlayer2(uId2);
    
        Scoreboard scoreboard = new Scoreboard();
        List<ScoreboardEntry> scoreboardEntrys = new ArrayList<>();
        ScoreboardEntry entry1 = new ScoreboardEntry(uId1 + 3, 0.0);
        ScoreboardEntry entry2 = new ScoreboardEntry(uId2, 0.0);
        scoreboardEntrys.add(entry1);
        scoreboardEntrys.add(entry2);
        scoreboard.setScoreboardEntries(scoreboardEntrys);
    
        testRound.setScoreboard(scoreboard);
    
        Tournament tournament = new Tournament();
        testRound.setTournament(tournament);
        tournament.setRounds(List.of(testRound));
    
        // Act
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            matchService.updateRoundScoreboard(testRound, testMatch, 1);
        });
    
        // Assert
        assertEquals("Player not found in scoreboard", exception.getMessage());
    }
    
    @Test
    void updateRoundScoreboard_Player2MissingFromScoreboard_Failure_PlayerNotFoundException() throws Exception {
        // Arrange
        Round testRound = new Round();
        Match testMatch = new Match();
        Long uId1 = (long) 1;
        Long uId2 = uId1 + 1;
        testMatch.setPlayer1(uId1);
        testMatch.setPlayer2(uId2);
    
        Scoreboard scoreboard = new Scoreboard();
        List<ScoreboardEntry> scoreboardEntrys = new ArrayList<>();
        ScoreboardEntry entry1 = new ScoreboardEntry(uId1, 0.0);
        ScoreboardEntry entry2 = new ScoreboardEntry(uId2 + 3, 0.0);
        scoreboardEntrys.add(entry1);
        scoreboardEntrys.add(entry2);
        scoreboard.setScoreboardEntries(scoreboardEntrys);
    
        testRound.setScoreboard(scoreboard);
    
        Tournament tournament = new Tournament();
        testRound.setTournament(tournament);
        tournament.setRounds(List.of(testRound));
    
        // Act
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            matchService.updateRoundScoreboard(testRound, testMatch, 1);
        });
    
        // Assert
        assertEquals("Player not found in scoreboard", exception.getMessage());
    }
    
    @Test
    void updateRoundScoreboard_SortScoreboardExceptionMissingMatch_Failure_TiebreakError() throws Exception {
        // Arrange
        Round testRound = new Round();
        Match testMatch = new Match();
        Match testMatch2 = new Match();
        Long uId1 = (long) 1;
        Long uId2 = uId1 + 1;
        Long uId3 = uId2 + 1;
        Long uId4 = uId3 + 1;
        Double elo1 = (double) 100;
        Double elo2 = elo1 + 100;
        Double elo3 = elo1 + 200;
        User testPlayer1 = new User();
        User testPlayer2 = new User();
        User opp1 = new User();
        User opp2 = new User();
    
        testPlayer1.setId(uId1);
        testPlayer1.setElo(elo1);
        testPlayer2.setId(uId2);
        testPlayer2.setElo(elo1);
        opp1.setId(uId3);
        opp1.setElo(elo2);
        opp2.setId(uId4);
        opp2.setElo(elo3);
        testMatch.setPlayer1(uId1);
        testMatch.setPlayer2(uId2);
        testMatch2.setPlayer1(uId3);
        testMatch2.setPlayer2(uId2);
    
        Scoreboard scoreboard = new Scoreboard();
        List<ScoreboardEntry> scoreboardEntrys = new ArrayList<>();
        ScoreboardEntry entry1 = new ScoreboardEntry(uId1, 0.0);
        ScoreboardEntry entry2 = new ScoreboardEntry(uId2, 0.0);
        ScoreboardEntry entry3 = new ScoreboardEntry(uId3, 0.5);
        ScoreboardEntry entry4 = new ScoreboardEntry(uId4, 0.5);
        scoreboardEntrys.add(entry1);
        scoreboardEntrys.add(entry2);
        scoreboardEntrys.add(entry3);
        scoreboardEntrys.add(entry4);
        scoreboard.setScoreboardEntries(scoreboardEntrys);
    
        testRound.setScoreboard(scoreboard);
        Tournament tournament = new Tournament();
        testRound.setTournament(tournament);
        tournament.setRounds(List.of(testRound));
    
        when(userRepository.findById(uId1)).thenReturn(Optional.of(testPlayer1));
        when(userRepository.findById(uId2)).thenReturn(Optional.of(testPlayer2));
        when(matchRepository.findByRoundAndPlayer1OrRoundAndPlayer2(testRound, uId1, testRound, uId1))
                .thenReturn(null);
    
        // Act
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            matchService.updateRoundScoreboard(testRound, testMatch, 0);
        });
    
        // Assert
        assertEquals("Error during tiebreak calculation", exception.getMessage());
    }
    
    @Test
    void getResult_MatchFound_Success_ReturnResult() throws Exception {
        Match testMatch = new Match();
        long mId = (long) 1;
        int resultTest = 1;
        testMatch.setResult(resultTest);
        testMatch.setId(mId);
        when(matchRepository.findById(mId)).thenReturn(Optional.of(testMatch));
    
        int result = matchService.getResult(mId);
    
        assertEquals(resultTest, result);
        verify(matchRepository).findById(mId);
    }
    
    @Test
    void getResult_MatchNotFound_Failure_MatchNotFoundException() throws Exception {
        long mId = (long) 1;
    
        when(matchRepository.findById(mId)).thenReturn(Optional.empty());
        boolean exceptionThrown = false;
        try {
            matchService.getResult(mId);
        } catch (Exception e) {
            assertEquals("Match not found", e.getMessage());
            exceptionThrown = true;
        }
    
        assertTrue(exceptionThrown);
        verify(matchRepository).findById(mId);
    }
}
