package com.codewithcled.fullstack_backend_proj1.IntegrationTests;

import org.apache.catalina.connector.Response;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

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

    //@Test
    void updateMatch_Success() throws Exception{
        Match testMatch=new Match();
        Round testRound=new Round();
        Tournament testTournament=new Tournament();
        User player1=new User();
        User player2=new User();
        User user = new User();
        Double elo=(double)1000;
        int outcome=0;

        player1.setUsername("testUser1");
        player1.setRole("ROLE_USER");
        player1.setEmail("testUser1");
        player1.setElo(elo);

        player2.setUsername("testUser2");
        player2.setRole("ROLE_USER");
        player2.setEmail("testUser2");
        player2.setElo(elo);

        testTournament.setTournament_name("testTournament");
        testTournament.setSize(2);
        testTournament.setNoOfRounds(1);
        testTournament.setStatus("active");
        testTournament.setDate("10/20/1203");

        List<Tournament> tournamentList = new ArrayList<>();
        List<User> userList = new ArrayList<>();
        tournamentList.add(testTournament);
        userList.add(player1);
        userList.add(player2);

        user.setCurrentTournaments(tournamentList);
        testTournament.setParticipants(userList);

        tournamentRepository.save(testTournament);
        player1=userRepository.save(player1);
        player2=userRepository.save(player2);

        testMatch.setPlayer1(player1.getId());
        testMatch.setPlayer2(player2.getId());
        testMatch.setIsComplete(false);
        testMatch.setRound(testRound);

        testRound.setTournament(testTournament);
        testRound.setRoundNum(1);
        testRound.setScoreboard(testTournament.getScoreboard());

        testRound=roundRepository.save(testRound);
        
        testRound.setMatchList(List.of(testMatch));
        roundRepository.save(testRound);
        
        testMatch=matchRepository.save(testMatch);

        URI url=new URI(baseUrl+port+urlPrefix+"/match/"+testMatch.getId()+"/update");

        ResponseEntity<String> result=restTemplate.exchange(
            url, 
            HttpMethod.PUT,
            new HttpEntity<>(outcome),
            String.class);

        assertEquals(HttpStatus.OK,result.getStatusCode());
        assertEquals("Match result updated successfully",result.getBody());
    }
}
