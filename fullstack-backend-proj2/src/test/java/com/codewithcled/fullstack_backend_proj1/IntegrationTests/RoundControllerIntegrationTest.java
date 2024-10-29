package com.codewithcled.fullstack_backend_proj1.IntegrationTests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import com.codewithcled.fullstack_backend_proj1.DTO.MatchDTO;
import com.codewithcled.fullstack_backend_proj1.model.Match;
import com.codewithcled.fullstack_backend_proj1.model.Round;
import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.repository.MatchRepository;
import com.codewithcled.fullstack_backend_proj1.repository.RoundRepository;
import com.codewithcled.fullstack_backend_proj1.repository.TournamentRepository;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class RoundControllerIntegrationTest {
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
    private MatchRepository matchRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    private String urlPrefix = "/r";

    private Tournament testTournament;
    private User player1;
    private User player2;

    @BeforeEach
    public void setUp() {
        Tournament testTournament = new Tournament();
        User player1 = new User();
        User player2 = new User();
        String role = "ROLE_USER";
        String userName = "testUser";
        Double elo = (double) 1000;

        player1.setUsername(userName + 1);
        player1.setRole(role);
        player1.setEmail(userName + 1);
        player1.setElo(elo);

        player2.setUsername(userName + 2);
        player2.setRole(role);
        player2.setEmail(userName + 2);
        player2.setElo(elo);

        testTournament.setTournament_name("testTournament");
        testTournament.setSize(2);
        testTournament.setCurrentSize(2);
        testTournament.setNoOfRounds(1);
        testTournament.setStatus("active");
        testTournament.setDate("10/20/1203");

        Tournament savedTournament = tournamentRepository.save(testTournament);
        User savedPlayer1 = userRepository.save(player1);
        User savedPlayer2 = userRepository.save(player2);

        List<Tournament> tournamentList = new ArrayList<>();
        List<User> userList = new ArrayList<>();
        tournamentList.add(savedTournament);
        userList.add(savedPlayer1);
        userList.add(savedPlayer2);

        savedPlayer1.setCurrentTournaments(tournamentList);
        savedPlayer2.setCurrentTournaments(tournamentList);
        savedTournament.setParticipants(userList);

        this.testTournament = tournamentRepository.save(savedTournament);
        this.player1 = userRepository.save(savedPlayer1);
        this.player2 = userRepository.save(savedPlayer2);
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
        tournamentRepository.deleteAll();
        roundRepository.deleteAll();
        matchRepository.deleteAll();
    }

    @Test
    void checkRoundComplete_Success() throws Exception {
        Round testRound = new Round();
        Map<Long, Double> scoreboard = new HashMap<Long, Double>();
        scoreboard.put(player1.getId(), 0.0);
        scoreboard.put(player2.getId(), 0.0);
        testRound.setTournament(testTournament);
        testRound.setRoundNum(1);
        testRound.setScoreboard(scoreboard);

        Round savedRound = roundRepository.save(testRound);

        Match testMatch = new Match();
        testMatch.setPlayer1(player1.getId());
        testMatch.setPlayer2(player2.getId());
        testMatch.setIsComplete(false);
        testMatch.setRound(savedRound);
        savedRound.setMatchList(List.of(testMatch));

        roundRepository.save(savedRound);

        matchRepository.save(testMatch);

        URI url = new URI(baseUrl + port + urlPrefix + "/round/" + savedRound.getId() + "/checkComplete");

        ResponseEntity<String> result = restTemplate.getForEntity(url, String.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Successfully checked roundService.isComplete", result.getBody());

    }

    @Test
    void getAllMatches_Success_FoundMatches_ReturnMatches() throws Exception {
        Round testRound = new Round();

        Map<Long, Double> scoreboard = new HashMap<Long, Double>();
        scoreboard.put(player1.getId(), 0.0);
        scoreboard.put(player2.getId(), 0.0);
        testRound.setTournament(testTournament);
        testRound.setRoundNum(1);
        testRound.setScoreboard(scoreboard);

        Round savedRound = roundRepository.save(testRound);
        List<Round> roundList=new ArrayList<>();
        roundList.add(savedRound);
        testTournament.setRounds(roundList);

        Match testMatch = new Match();
        testMatch.setPlayer1(player1.getId());
        testMatch.setPlayer2(player2.getId());
        testMatch.setIsComplete(false);
        testMatch.setRound(savedRound);
        savedRound.setMatchList(List.of(testMatch));

        roundRepository.save(savedRound);

        matchRepository.save(testMatch);

        URI url = new URI(baseUrl + port + urlPrefix + "/round/" + savedRound.getId() + "/matches");

        ResponseEntity<List<MatchDTO>> result = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<MatchDTO>>() {
                });

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(testMatch.getPlayer1(), result.getBody().get(0).getPlayer1());
        assertEquals(testMatch.getPlayer2(), result.getBody().get(0).getPlayer2());
    }

    @Test
    void getAllMatches_Success_NoMatches_ReturnEmptyList() throws Exception {
        Round testRound=new Round();
        Map<Long, Double> scoreboard = new HashMap<Long, Double>();
        scoreboard.put(player1.getId(), 0.0);
        scoreboard.put(player2.getId(), 0.0);

        testRound.setTournament(testTournament);
        testRound.setRoundNum(1);
        testRound.setScoreboard(scoreboard);

        Round savedRound = roundRepository.save(testRound);

        URI url = new URI(baseUrl + port + urlPrefix + "/round/" + savedRound.getId() + "/matches");

        ResponseEntity<List<MatchDTO>> result = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<MatchDTO>>() {
                });

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }

    @Test
    void getAllMatches_Failure_RoundNotFound() throws Exception {
        URI url = new URI(baseUrl + port + urlPrefix + "/round/80/matches");

        ResponseEntity<Exception> result = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                Exception.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

}
