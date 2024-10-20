package com.codewithcled.fullstack_backend_proj1.UnitTests;

import org.apache.hc.client5.http.fluent.Request;
import org.hibernate.mapping.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import com.codewithcled.fullstack_backend_proj1.model.Match;
import com.codewithcled.fullstack_backend_proj1.model.Round;
import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.repository.MatchRepository;
import com.codewithcled.fullstack_backend_proj1.repository.TournamentRepository;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;
import com.codewithcled.fullstack_backend_proj1.service.EloRatingService;
import com.codewithcled.fullstack_backend_proj1.service.MatchServiceImplementation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class MatchServiceTest {
    @Mock
    private MatchRepository matchRepository;

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EloRatingService eloRatingService;

    @InjectMocks
    MatchServiceImplementation matchService;

    // @Test
    // public void createMatch_Success(){
    //     Double elo1=(double)1000;
    //     Double elo2=(double)1010;
    //     long uId1=(long)1;
    //     long uId2=(long)2;

    //     User p1=new User();
    //     p1.setElo(elo1);
    //     p1.setId(uId1);

    //     User p2=new User();
    //     p1.setElo(elo2);
    //     p1.setId(uId2);

    //     Match result=matchService.createMatch(p1, p2);

    //     assertEquals(elo1,result.getPlayer1StartingElo());
    //     assertEquals(elo2,result.getPlayer2StartingElo());
    //     assertEquals(uId1,result.getPlayer1());
    //     assertEquals(uId2,result.getPlayer2());
    // }

    @Test
    void getResult_Success() throws Exception{
        Match testMatch=new Match();
        long mId=(long)1;
        int resultTest=1;
        testMatch.setResult(resultTest);
        testMatch.setId(mId);
        when(matchRepository.findById(mId)).thenReturn(Optional.of(testMatch));

        int result=matchService.getResult(mId);

        assertEquals(resultTest,result);
        verify(matchRepository).findById(mId);
    }

    @Test
    void result_Failure() throws Exception{
        long mId=(long)1;

        when(matchRepository.findById(mId)).thenReturn(Optional.empty());
        boolean exceptionThrown=false;
        try {
            matchService.getResult(mId);
        } catch (Exception e) {
            assertEquals("Match not found",e.getMessage());
            exceptionThrown=true;
        }

        assertEquals(true,exceptionThrown);
        verify(matchRepository).findById(mId);
    }

    @Test
    void getEloChange1_Success() throws Exception{
        Match testMatch=new Match();
        long mId=(long)1;
        double eloChange=10.0;
        testMatch.setEloChange1(eloChange);
        testMatch.setId(mId);

        when(matchRepository.findById(mId)).thenReturn(Optional.of(testMatch));

        double result=matchService.getEloChange1(mId);

        assertEquals(eloChange,result);
        verify(matchRepository).findById(mId);
    }

    @Test
    void getEloChange1_Failure() throws Exception{
        long mId=(long)1;

        when(matchRepository.findById(mId)).thenReturn(Optional.empty());
        boolean exceptionThrown=false;
        try {
            matchService.getEloChange1(mId);
        } catch (Exception e) {
            assertEquals("Match not found",e.getMessage());
            exceptionThrown=true;
        }

        assertEquals(true,exceptionThrown);
        verify(matchRepository).findById(mId);
    }

    @Test
    void getEloChange2_Success() throws Exception{
        Match testMatch=new Match();
        long mId=(long)1;
        double eloChange=10.0;
        testMatch.setEloChange2(eloChange);
        testMatch.setId(mId);

        when(matchRepository.findById(mId)).thenReturn(Optional.of(testMatch));

        double result=matchService.getEloChange2(mId);

        assertEquals(eloChange,result);
        verify(matchRepository).findById(mId);
    }

    @Test
    void getEloChange2_Failure() throws Exception{
        long mId=(long)1;

        when(matchRepository.findById(mId)).thenReturn(Optional.empty());
        boolean exceptionThrown=false;
        try {
            matchService.getEloChange2(mId);
        } catch (Exception e) {
            assertEquals("Match not found",e.getMessage());
            exceptionThrown=true;
        }

        assertEquals(true,exceptionThrown);
        verify(matchRepository).findById(mId);
    }
}
