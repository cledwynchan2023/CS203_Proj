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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import com.codewithcled.fullstack_backend_proj1.DTO.ResultRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.SignInRequest;
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
import com.codewithcled.fullstack_backend_proj1.response.AuthResponse;
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

    @Autowired
    PasswordEncoder passwordEncoder;

    private Tournament testTournament;
    private User player1;
    private User player2;
    private Round testRound;
    private String JWT;

    @BeforeEach
    public void setUp() throws Exception{
        String role="ROLE_USER";
        String userName="testUser";
        Double elo=(double)1000;

        User user1=new User();
        user1.setUsername(userName+1);
        user1.setRole(role);
        user1.setEmail(userName+1);
        user1.setElo(elo);
        user1.setPassword(userName);

        User user2=new User();
        user2.setUsername(userName+2);
        user2.setRole(role);
        user2.setEmail(userName+2);
        user2.setElo(elo);
        user1.setPassword(userName);

        Tournament tournament=new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(2);
        tournament.setCurrentSize(2);
        tournament.setNoOfRounds(2);
        tournament.setStatus("active");
        tournament.setDate("10/20/1203");

        //create Tournament and Users
        this.testTournament=tournamentRepository.save(tournament);
        this.player1=userRepository.save(user1);
        this.player2=userRepository.save(user2);

        //create tournament and user relationships
        player1.addCurrentTournament(testTournament);
        player2.addCurrentTournament(testTournament);
        testTournament.addParticipant(player1);
        testTournament.addParticipant(player2);

        tournamentRepository.save(testTournament);
        userRepository.save(player1);
        userRepository.save(player2);

        Scoreboard scoreboard=new Scoreboard();
        List<ScoreboardEntry> scoreboardEntrys=new ArrayList<>();
        ScoreboardEntry entry1=new ScoreboardEntry(player1.getId(), 0.0);
        ScoreboardEntry entry2=new ScoreboardEntry(player2.getId(), 0.0);
        scoreboardEntrys.add(entry1);
        scoreboardEntrys.add(entry2);
        scoreboard.setScoreboardEntries(scoreboardEntrys);

        Round round=new Round();
        round.setTournament(testTournament);
        round.setRoundNum(1);
        round.setScoreboard(null);
        round.setScoreboard(scoreboard);
        round.setIsCompleted(false);
        round.setMatchList(new ArrayList<>());
        this.testRound=roundRepository.save(round);

        List<Round> roundList=testTournament.getRounds();
        roundList.add(testRound);
        tournamentRepository.save(testTournament);

        User admin = new User();
        admin.setUsername("AdminUser");
        admin.setEmail("AdminUser");
        admin.setPassword(passwordEncoder.encode("Admin"));
        admin.setRole("ROLE_ADMIN");
        userRepository.save(admin);

        URI uri = new URI(baseUrl + port + "/auth/signin");

        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setUsername("AdminUser");
        signInRequest.setPassword("Admin");

        ResponseEntity<AuthResponse> result = restTemplate.postForEntity(uri, signInRequest, AuthResponse.class);

        JWT = result.getBody().getJwt();
    }

    @AfterEach
    public void tearDown() {
        matchRepository.deleteAll();
        userRepository.deleteAll();
        tournamentRepository.deleteAll();
        roundRepository.deleteAll();
    }

    //@Test//Works until it calls to checkComplete round then fails as connection is refused
    void updateMatch_Success() throws Exception{
        Match testMatch=new Match();
        testMatch.setPlayer1(player1.getId());
        testMatch.setPlayer1StartingElo(player2.getElo());
        testMatch.setPlayer2(player1.getId());
        testMatch.setPlayer2StartingElo(player2.getElo());
        testMatch.setRound(testRound);
        testMatch.setEloChange1(0.0);
        testMatch.setEloChange2(0.0);

        Match saveMatch=matchRepository.save(testMatch);

        List<Match> roundMatchList=testRound.getMatchList();
        roundMatchList.add(saveMatch);
        roundRepository.save(testRound);

        ResultRequest outcome=new ResultRequest();
        outcome.setResult(0);

        URI url=new URI(baseUrl+port+urlPrefix+"/match/"+saveMatch.getId()+"/update");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + JWT);

        ResponseEntity<String> result=restTemplate.exchange(
            url,
            HttpMethod.PUT,
            new HttpEntity<>(outcome,headers),
            String.class);

        assertEquals("Match result updated successfully",result.getBody());
        assertEquals(HttpStatus.OK,result.getStatusCode());
        
    }

    @Test
    public void getPlayers_Success_ReturnPlayerUserNameList() throws Exception{
        Match match = new Match();
        match.setPlayer1(player1.getId());
        match.setPlayer2(player2.getId());
        match.setPlayer1StartingElo(player1.getElo());
        match.setPlayer2StartingElo(player2.getElo());
        match.setIsComplete(false);
        match.setRound(testRound);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + JWT);
        
        Match savedMatch=matchRepository.save(match);

        URI url=new URI(baseUrl+port+urlPrefix+"/match/"+savedMatch.getId()+"/getPlayers");

        ResponseEntity<String[]> result=restTemplate.exchange(
            url,
            HttpMethod.GET,
            new HttpEntity<>(null,headers),
            String[].class);

        assertEquals(HttpStatus.OK,result.getStatusCode());
        assertEquals(player1.getUsername(),result.getBody()[0]);
        assertEquals(player2.getUsername(),result.getBody()[1]);
    }
}
