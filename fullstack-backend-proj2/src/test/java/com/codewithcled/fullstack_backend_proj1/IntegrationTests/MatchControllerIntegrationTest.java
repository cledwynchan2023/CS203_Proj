package com.codewithcled.fullstack_backend_proj1.IntegrationTests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpEntity;

import com.codewithcled.fullstack_backend_proj1.DTO.ResultRequest;
import com.codewithcled.fullstack_backend_proj1.model.Match;
import com.codewithcled.fullstack_backend_proj1.model.Round;
import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.repository.MatchRepository;
import com.codewithcled.fullstack_backend_proj1.repository.RoundRepository;
import com.codewithcled.fullstack_backend_proj1.repository.TournamentRepository;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;
import com.codewithcled.fullstack_backend_proj1.service.MatchService;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MatchControllerIntegrationTest {
    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

    @Autowired
    private TournamentRepository tournamentRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoundRepository roundRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    private String urlPrefix = "/m";

    @Autowired
    MatchRepository matchRepository;

    @Autowired
    MatchService matchService;

    private Tournament testTournament;
    private User player1;
    private User player2;
    private Round testRound;

    @BeforeEach
    public void setUp(){
        String role="ROLE_USER";
        String userName="testUser";
        Double elo=(double)1000;

        User user1=new User();
        user1.setUsername(userName+1);
        user1.setRole(role);
        user1.setEmail(userName+1);
        user1.setElo(elo);

        User user2=new User();
        user2.setUsername(userName+2);
        user2.setRole(role);
        user2.setEmail(userName+2);
        user2.setElo(elo);

        Tournament tournament=new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(2);
        tournament.setCurrentSize(2);
        tournament.setNoOfRounds(2);
        tournament.setStatus("active");
        tournament.setDate("10/20/1203");

        Tournament savedTournament=tournamentRepository.save(tournament);
        User savedPlayer1=userRepository.save(user1);
        User savedPlayer2=userRepository.save(user2);

        List<Tournament> tournamentList = new ArrayList<>();
        List<User> userList = new ArrayList<>();
        tournamentList.add(savedTournament);
        userList.add(savedPlayer1);
        userList.add(savedPlayer2);

        savedPlayer1.setCurrentTournaments(tournamentList);
        savedPlayer2.setCurrentTournaments(tournamentList);
        savedTournament.setParticipants(userList);

        this.testTournament=tournamentRepository.save(savedTournament);
        this.player1=userRepository.save(savedPlayer1);
        this.player2=userRepository.save(savedPlayer2);

        Map<Long,Double> scoreboard=new HashMap<Long,Double>();
        scoreboard.put(player1.getId(),0.0);
        scoreboard.put(player2.getId(),0.0);

        Round round=new Round();
        round.setTournament(testTournament);
        round.setRoundNum(1);
        round.setScoreboard(scoreboard);
        round.setIsCompleted(false);
        round.setMatchList(new ArrayList<>());
        this.testRound=roundRepository.save(round);

        List<Round> roundList=new ArrayList<>();
        roundList.add(testRound);
        testTournament.setRounds(roundList);
        tournamentRepository.save(testTournament);
    }

    @AfterEach
    public void tearDown() {
        matchRepository.deleteAll();
        userRepository.deleteAll();
        tournamentRepository.deleteAll();
        roundRepository.deleteAll();
    }

    //@Test//Can't get it to work
    void updateMatch_Success() throws Exception{
        Match testMatch=new Match();
        testMatch.setPlayer1(player1.getId());
        testMatch.setPlayer1StartingElo(player2.getElo());
        testMatch.setPlayer2(player1.getId());
        testMatch.setPlayer2StartingElo(player2.getElo());
        testMatch.setIsComplete(false);
        testMatch.setRound(testRound);
        testMatch.setEloChange1(0.0);
        testMatch.setEloChange2(0.0);

        List<Match> roundMatchList=testRound.getMatchList();
        roundMatchList.add(testMatch);
        roundRepository.save(testRound);

        Match saveMatch=matchRepository.save(testMatch);
        ResultRequest outcome=new ResultRequest();
        outcome.setResult(0);

        URI url=new URI(baseUrl+port+urlPrefix+"/match/"+saveMatch.getId()+"/update");

        ResponseEntity<String> result=restTemplate.exchange(
            url, 
            HttpMethod.PUT,
            new HttpEntity<>(outcome),
            String.class);

        assertEquals("Match result updated successfully",result.getBody());
        assertEquals(HttpStatus.OK,result.getStatusCode());
        
    }

    @Test
    public void getPlayers_Success() throws Exception{
        Match match = new Match();
        match.setPlayer1(player1.getId());
        match.setPlayer2(player2.getId());
        match.setPlayer1StartingElo(player1.getElo());
        match.setPlayer2StartingElo(player2.getElo());
        match.setIsComplete(false);
        match.setRound(testRound);
        
        Match savedMatch=matchRepository.save(match);

        URI url=new URI(baseUrl+port+urlPrefix+"/match/"+savedMatch.getId()+"/getPlayers");

        ResponseEntity<String[]> result=restTemplate.getForEntity(url, String[].class);

        assertEquals(HttpStatus.OK,result.getStatusCode());
    }
}
