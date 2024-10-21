package com.codewithcled.fullstack_backend_proj1.UnitTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

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

    @InjectMocks
    private RoundServiceImplementation roundService;

    //@Test
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

        when(tournamentRepository.findById(tId)).thenReturn(Optional.of(testTournament));
        when(roundRepository.save(testRound)).thenReturn(testRound);
        when(matchService.createMatch(testUser1, testUser2)).thenReturn(testMatch);
        when(matchRepository.save(testMatch)).thenReturn(testMatch);
        when(tournamentRepository.save(testTournament)).thenReturn(testTournament);

        Round result = roundService.createFirstRound(tId);

        assertEquals(1, result.getMatchList().size());
        assertEquals(1, result.getRoundNum());
        assertEquals(2, result.getScoreboard().size());
        assertEquals(testTournament, result.getTournament());

        verify(tournamentRepository).findById(tId);
        verify(roundRepository).save(testRound);
        verify(matchService).createMatch(testUser1, testUser2);
        verify(matchRepository).save(testMatch);
    }

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
    void checkComplete_Success_NotAllMatchComplete() throws Exception{
        Long rId=(long)24234;
        Round testRound=new Round();
        Match testMatch=new Match();
        testMatch.setIsComplete(false);
        testRound.setMatchList(List.of(testMatch));

        when(roundRepository.findById(rId)).thenReturn(Optional.of(testRound));

        roundService.checkComplete(rId);

        verify(roundRepository).findById(rId);
    }

    //@Test
    void checkComplete_Success_AllMatchComplete() throws Exception{
        //Circular dependency issue
        Long rId=(long)24234;
        Round testRound=new Round();
        Match testMatch=new Match();
        testMatch.setIsComplete(true);
        testRound.setMatchList(List.of(testMatch));

        when(roundRepository.findById(rId)).thenReturn(Optional.of(testRound));

        roundService.checkComplete(rId);

        verify(roundRepository).findById(rId);
    }

    @Test
    void checkComplete_Failure() throws Exception{
        Long rId=(long)24234;

        when(roundRepository.findById(rId)).thenReturn(Optional.empty());
        boolean exceptionThrown=false;

        try {
            roundService.checkComplete(rId);
        } catch (Exception e) {
            assertEquals("Round not found",e.getMessage());
            exceptionThrown=true;
        }
        
        assertTrue(exceptionThrown);
        verify(roundRepository).findById(rId);
    }

    // @Test
    void createNextRound_Success() throws Exception {
        // Can't test returns a mocked newRound, would just return testRound due to
        // roundRepository.save()
        Long tId = (long) 132;
        Tournament testTournament = new Tournament();
        testTournament.setId(tId);
        Round testRound = new Round();
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
        Map<Long, Double> scoreboard = new HashMap<Long,Double>();
        scoreboard.put(tId + 1, 0.0);
        scoreboard.put(tId + 2, 0.0);
        testTournament.setScoreboard(scoreboard);

        when(tournamentRepository.findById(tId)).thenReturn(Optional.of(testTournament));
        when(userRepository.findById(tId + 1)).thenReturn(Optional.of(testUser1));
        when(userRepository.findById(tId + 2)).thenReturn(Optional.of(testUser2));
        when(matchService.createMatch(testUser1, testUser2)).thenReturn(new Match());
        when(roundRepository.save(testRound)).thenReturn(testRound);

        Round result = roundService.createNextRound(tId);

        verify(tournamentRepository).findById(tId);
        verify(userRepository).findById(tId + 1);
        verify(userRepository).findById(tId + 2);
        verify(matchService).createMatch(testUser1, testUser2);
        verify(roundRepository).save(testRound);
    }

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
    void createNextRound_Failure_UserNotFound() throws Exception {
        Long tId = (long) 132;
        Tournament testTournament = new Tournament();
        testTournament.setId(tId);
        Round testRound = new Round();
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
        Map<Long, Double> scoreboard = new HashMap<Long,Double>();
        scoreboard.put(tId + 1, 0.0);
        scoreboard.put(tId + 2, 0.0);
        testTournament.setScoreboard(scoreboard);

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
}
