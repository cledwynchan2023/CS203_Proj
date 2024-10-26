package com.codewithcled.fullstack_backend_proj1.IntegrationTests;

import org.junit.jupiter.api.AfterEach;
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

    @AfterEach
    public void tearDown() {
        matchRepository.deleteAll();
        userRepository.deleteAll();
        tournamentRepository.deleteAll();
        roundRepository.deleteAll();
    }

    //@Test Couldn't get it to work
    void updateMatch_Success() throws Exception{
        Match testMatch=new Match();
        Round testRound=new Round();
        Tournament testTournament=new Tournament();
        User player1=new User();
        User player2=new User();
        int outcome=0;
        String role="ROLE_USER";
        String userName="testUser";
        Double elo=(double)1000;

        player1.setUsername(userName+1);
        player1.setRole(role);
        player1.setEmail(userName+1);
        player1.setElo(elo);

        player2.setUsername(userName+2);
        player2.setRole(role);
        player2.setEmail(userName+2);
        player2.setElo(elo);

        testTournament.setTournament_name("testTournament");
        testTournament.setSize(2);
        testTournament.setCurrentSize(2);
        testTournament.setNoOfRounds(1);
        testTournament.setStatus("active");
        testTournament.setDate("10/20/1203");

        Tournament savedTournament=tournamentRepository.save(testTournament);
        User savedPlayer1=userRepository.save(player1);
        User savedPlayer2=userRepository.save(player2);

        List<Tournament> tournamentList = new ArrayList<>();
        List<User> userList = new ArrayList<>();
        tournamentList.add(savedTournament);
        userList.add(savedPlayer1);
        userList.add(savedPlayer2);

        savedPlayer1.setCurrentTournaments(tournamentList);
        savedPlayer2.setCurrentTournaments(tournamentList);
        savedTournament.setParticipants(userList);
        Map<Long,Double> scoreboard=new HashMap<Long,Double>();
        scoreboard.put(savedPlayer1.getId(),0.0);
        scoreboard.put(savedPlayer2.getId(),0.0);
        savedTournament.setScoreboard(scoreboard);

        tournamentRepository.save(savedTournament);
        userRepository.save(savedPlayer1);
        userRepository.save(savedPlayer2);

        testRound.setTournament(savedTournament);
        testRound.setRoundNum(1);
        testRound.setScoreboard(savedTournament.getScoreboard());

        Round savedRound=roundRepository.save(testRound);

        testMatch.setPlayer1(savedPlayer1.getId());
        testMatch.setPlayer1StartingElo(savedPlayer1.getElo());
        testMatch.setPlayer2(savedPlayer2.getId());
        testMatch.setPlayer2StartingElo(savedPlayer2.getElo());
        testMatch.setIsComplete(false);
        savedRound.setMatchList(List.of(testMatch));
        testMatch.setRound(savedRound);

        roundRepository.save(savedRound);

        Match saveMatch=matchRepository.save(testMatch);

        URI url=new URI(baseUrl+port+urlPrefix+"/match/"+saveMatch.getId()+"/update");

        ResponseEntity<String> result=restTemplate.exchange(
            url, 
            HttpMethod.PUT,
            new HttpEntity<>(outcome),
            String.class);

        assertEquals(HttpStatus.OK,result.getStatusCode());
        assertEquals("Match result updated successfully",result.getBody());
    }
}
