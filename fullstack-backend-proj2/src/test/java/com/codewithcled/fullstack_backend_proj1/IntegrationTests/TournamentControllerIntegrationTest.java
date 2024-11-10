package com.codewithcled.fullstack_backend_proj1.IntegrationTests;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.util.UriComponentsBuilder;

import com.codewithcled.fullstack_backend_proj1.DTO.CreateTournamentRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.RoundDTO;
import com.codewithcled.fullstack_backend_proj1.DTO.TournamentDTO;
import com.codewithcled.fullstack_backend_proj1.DTO.TournamentStartDTO;
import com.codewithcled.fullstack_backend_proj1.DTO.UserDTO;
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

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TournamentControllerIntegrationTest {
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

    @Autowired
    PasswordEncoder passwordEncoder;

    private String urlPrefix = "/t";
    private User user1;
    private User user2;

    @BeforeEach
    public void setUp() {
        User testUser1 = new User();
        testUser1.setUsername("testUser1");
        testUser1.setRole("ROLE_USER");
        testUser1.setEmail("testUser1");
        testUser1.setPassword(passwordEncoder.encode("testUser1"));
        testUser1.setElo((double) 100);
        testUser1.setCurrentTournaments(new ArrayList<>());

        User testUser2 = new User();
        testUser2.setUsername("testUser2");
        testUser2.setRole("ROLE_USER");
        testUser2.setEmail("testUser2");
        testUser2.setPassword(passwordEncoder.encode("testUser2"));
        testUser2.setElo((double) 100);
        testUser2.setCurrentTournaments(new ArrayList<>());

        this.user1 = userRepository.save(testUser1);
        this.user2 = userRepository.save(testUser2);
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
        tournamentRepository.deleteAll();
        roundRepository.deleteAll();
        matchRepository.deleteAll();
    }

    @Test
    public void getAllTournaments_Success() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(0);
        tournament.setNoOfRounds(0);
        tournament.setStatus("Active");
        tournament.setDate("10/20/1203");
        tournamentRepository.save(tournament);

        URI url = new URI(baseUrl + port + urlPrefix + "/tournaments");

        ResponseEntity<List<TournamentDTO>> result = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TournamentDTO>>() {
                });

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("testTournament", result.getBody().get(0).getTournamentName());
    }

    @Test
    public void getAllTournaments_Failure() throws Exception {
        URI url = new URI(baseUrl + port + urlPrefix + "/tournaments");

        ResponseEntity<List<TournamentDTO>> result = restTemplate.exchange(url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TournamentDTO>>() {
                });

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }

    @Test
    public void getActiveTournaments_Success_ReturnActiveTournaments() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(0);
        tournament.setCurrentSize(0);
        tournament.setNoOfRounds(0);
        tournament.setStatus("active");
        tournament.setDate("10/20/1203");
        tournamentRepository.save(tournament);

        Tournament tournament2 = new Tournament();
        tournament2.setTournament_name("testTournament2");
        tournament2.setSize(0);
        tournament2.setCurrentSize(0);
        tournament2.setNoOfRounds(0);
        tournament2.setStatus("ongoing");
        tournament2.setDate("10/20/1203");
        tournamentRepository.save(tournament2);

        Tournament tournament3 = new Tournament();
        tournament3.setTournament_name("testTournament3");
        tournament3.setSize(0);
        tournament3.setCurrentSize(0);
        tournament3.setNoOfRounds(0);
        tournament3.setStatus("completed");
        tournament3.setDate("10/20/1203");
        tournamentRepository.save(tournament3);
        URI url = new URI(baseUrl + port + urlPrefix + "/tournaments/active");

        ResponseEntity<List<TournamentDTO>> result = restTemplate.exchange(url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TournamentDTO>>() {
                });

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        assertEquals("testTournament", result.getBody().get(0).getTournamentName());
    }

    @Test
    public void getActiveTournaments_Success_NoTournaments_ReturnEmptyList() throws Exception {
        URI url = new URI(baseUrl + port + urlPrefix + "/tournaments/active");

        ResponseEntity<List<TournamentDTO>> result = restTemplate.exchange(url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TournamentDTO>>() {
                });

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }

    @Test
    public void getCompletedTournaments_Success_ReturnCompletedTournaments() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(0);
        tournament.setCurrentSize(0);
        tournament.setNoOfRounds(0);
        tournament.setStatus("active");
        tournament.setDate("10/20/1203");
        tournamentRepository.save(tournament);

        Tournament tournament2 = new Tournament();
        tournament2.setTournament_name("testTournament2");
        tournament2.setSize(0);
        tournament2.setCurrentSize(0);
        tournament2.setNoOfRounds(0);
        tournament2.setStatus("ongoing");
        tournament2.setDate("10/20/1203");
        tournamentRepository.save(tournament2);

        Tournament tournament3 = new Tournament();
        tournament3.setTournament_name("testTournament3");
        tournament3.setSize(0);
        tournament3.setCurrentSize(0);
        tournament3.setNoOfRounds(0);
        tournament3.setStatus("completed");
        tournament3.setDate("10/20/1203");
        tournamentRepository.save(tournament3);

        URI url = new URI(baseUrl + port + urlPrefix + "/tournaments/completed");

        ResponseEntity<List<TournamentDTO>> result = restTemplate.exchange(url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TournamentDTO>>() {
                });

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        assertEquals("testTournament3", result.getBody().get(0).getTournamentName());
    }

    @Test
    public void getCompletedTournaments_Success_NoTournaments_ReturnEmptyList() throws Exception {
        URI url = new URI(baseUrl + port + urlPrefix + "/tournaments/completed");

        ResponseEntity<List<TournamentDTO>> result = restTemplate.exchange(url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TournamentDTO>>() {
                });

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }

    @Test
    public void getOngoingTournaments_Success_NoTournaments_ReturnOngoingTournaments() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(0);
        tournament.setCurrentSize(0);
        tournament.setNoOfRounds(0);
        tournament.setStatus("active");
        tournament.setDate("10/20/1203");
        tournamentRepository.save(tournament);

        Tournament tournament2 = new Tournament();
        tournament2.setTournament_name("testTournament2");
        tournament2.setSize(0);
        tournament2.setCurrentSize(0);
        tournament2.setNoOfRounds(0);
        tournament2.setStatus("ongoing");
        tournament2.setDate("10/20/1203");
        tournamentRepository.save(tournament2);

        Tournament tournament3 = new Tournament();
        tournament3.setTournament_name("testTournament3");
        tournament3.setSize(0);
        tournament3.setCurrentSize(0);
        tournament3.setNoOfRounds(0);
        tournament3.setStatus("completed");
        tournament3.setDate("10/20/1203");
        tournamentRepository.save(tournament3);

        URI url = new URI(baseUrl + port + urlPrefix + "/tournaments/ongoing");

        ResponseEntity<List<TournamentDTO>> result = restTemplate.exchange(url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TournamentDTO>>() {
                });

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        assertEquals("testTournament2", result.getBody().get(0).getTournamentName());
    }

    @Test
    public void getOngoingTournaments_Success_NoTournaments_ReturnEmptyList() throws Exception {
        URI url = new URI(baseUrl + port + urlPrefix + "/tournaments/ongoing");

        ResponseEntity<List<TournamentDTO>> result = restTemplate.exchange(url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TournamentDTO>>() {
                });

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }

    @Test
    public void getFilteredTournamentsByName_Success_ReturnTournamentListFilteredByName() throws Exception {
        Tournament testTournament1 = new Tournament();
        testTournament1.setTournament_name("t1");
        testTournament1.setDate("10/5/2024");
        testTournament1.setSize(4);
        testTournament1.setCurrentSize(3);
        Tournament testTournament2 = new Tournament();
        testTournament2.setTournament_name("t2");
        testTournament2.setDate("12/5/2024");
        testTournament2.setSize(4);
        testTournament2.setCurrentSize(1);
        Tournament testTournament3 = new Tournament();
        testTournament3.setTournament_name("t3");
        testTournament3.setDate("13/5/2024");
        testTournament3.setSize(4);
        testTournament3.setCurrentSize(2);

        tournamentRepository.save(testTournament3);
        tournamentRepository.save(testTournament1);
        tournamentRepository.save(testTournament2);

        URI url = new URI(baseUrl + port + urlPrefix + "/tournaments/name");

        ResponseEntity<List<TournamentDTO>> result = restTemplate.exchange(url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TournamentDTO>>() {
                });

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(3, result.getBody().size());
        assertEquals("t1", result.getBody().get(0).getTournamentName());
        assertEquals("t2", result.getBody().get(1).getTournamentName());
        assertEquals("t3", result.getBody().get(2).getTournamentName());
    }

    @Test
    public void getFilteredTournamentsByName_Success_NoTournaments_ReturnEmptyList() throws Exception {

        URI url = new URI(baseUrl + port + urlPrefix + "/tournaments/name");

        ResponseEntity<List<TournamentDTO>> result = restTemplate.exchange(url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TournamentDTO>>() {
                });

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }

    @Test
    public void getFilteredTournamentsByDate_Success_ReturnTournamentListFilteredByDate() throws Exception {
        Tournament testTournament1 = new Tournament();
        testTournament1.setTournament_name("t1");
        testTournament1.setDate("10/5/2024");
        testTournament1.setSize(4);
        testTournament1.setCurrentSize(3);
        Tournament testTournament2 = new Tournament();
        testTournament2.setTournament_name("t2");
        testTournament2.setDate("12/5/2024");
        testTournament2.setSize(4);
        testTournament2.setCurrentSize(1);
        Tournament testTournament3 = new Tournament();
        testTournament3.setTournament_name("t3");
        testTournament3.setDate("13/5/2024");
        testTournament3.setSize(4);
        testTournament3.setCurrentSize(2);

        tournamentRepository.save(testTournament3);
        tournamentRepository.save(testTournament1);
        tournamentRepository.save(testTournament2);

        URI url = new URI(baseUrl + port + urlPrefix + "/tournaments/date");

        ResponseEntity<List<TournamentDTO>> result = restTemplate.exchange(url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TournamentDTO>>() {
                });

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(3, result.getBody().size());
        assertEquals("13/5/2024", result.getBody().get(0).getDate());
        assertEquals("12/5/2024", result.getBody().get(1).getDate());
        assertEquals("10/5/2024", result.getBody().get(2).getDate());
    }

    @Test
    public void getFilteredTournamentsByDate_Success_NoTournaments_ReturnEmptyList() throws Exception {
        URI url = new URI(baseUrl + port + urlPrefix + "/tournaments/date");

        ResponseEntity<List<TournamentDTO>> result = restTemplate.exchange(url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TournamentDTO>>() {
                });

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }

    @Test
    public void getFilteredTournamentsBySize_Success_ReturnTournamentListFilteredByFreeSize() throws Exception {
        URI url = new URI(baseUrl + port + urlPrefix + "/tournaments/capacity");
        Tournament testTournament1 = new Tournament();
        testTournament1.setTournament_name("t1");
        testTournament1.setDate("10/5/2024");
        testTournament1.setSize(8);
        testTournament1.setCurrentSize(7);// free size 1
        Tournament testTournament2 = new Tournament();
        testTournament2.setTournament_name("t2");
        testTournament2.setDate("12/5/2024");
        testTournament2.setSize(7);
        testTournament2.setCurrentSize(4);// free size 3
        Tournament testTournament3 = new Tournament();
        testTournament3.setTournament_name("t3");
        testTournament3.setDate("13/5/2024");
        testTournament3.setSize(4);
        testTournament3.setCurrentSize(2);// free size 2

        tournamentRepository.save(testTournament3);
        tournamentRepository.save(testTournament1);
        tournamentRepository.save(testTournament2);

        ResponseEntity<List<TournamentDTO>> result = restTemplate.exchange(url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TournamentDTO>>() {
                });

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(3, result.getBody().size());
        assertEquals("t2", result.getBody().get(0).getTournamentName());
        assertEquals("t3", result.getBody().get(1).getTournamentName());
        assertEquals("t1", result.getBody().get(2).getTournamentName());
    }

    @Test
    public void getFilteredTournamentsBySize_Success_NoTournaments_ReturnEmptyList() throws Exception {
        URI url = new URI(baseUrl + port + urlPrefix + "/tournaments/capacity");

        ResponseEntity<List<TournamentDTO>> result = restTemplate.exchange(url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TournamentDTO>>() {
                });

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }

    @Test
    public void getTournamentById_Success() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(0);
        tournament.setNoOfRounds(0);
        tournament.setDate("10/20/1203");
        tournament.setStatus("active");
        Tournament savedTournament = tournamentRepository.save(tournament);

        URI url = new URI(baseUrl + port + urlPrefix + "/" + savedTournament.getId());

        ResponseEntity<TournamentDTO> result = restTemplate.getForEntity(url, TournamentDTO.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("testTournament", result.getBody().getTournamentName());
    }

    @Test
    public void getTournamentById_Failure() throws Exception {

        URI url = new URI(baseUrl + port + urlPrefix + "/2423");

        ResponseEntity<TournamentDTO> result = restTemplate.getForEntity(url, TournamentDTO.class);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    //@Test // Issue with sending the post getting unsupported Media Type Exception
    //Method is unused
    public void addRound_Success() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(2);
        tournament.setCurrentSize(2);
        tournament.setNoOfRounds(3);
        tournament.setDate("10/20/1203");
        tournament.setStatus("active");
        tournament.setRounds(new ArrayList<>());
        tournament.addParticipant(user1);
        tournament.addParticipant(user2);

        // Save the tournament to the repository
        Tournament savedTournament = tournamentRepository.save(tournament);

        // Create a Round object
        Round round = new Round();
        round.setIsCompleted(false);
        round.setTournament(null);
        round.setRoundNum(1);
        round.setId((long) 138021);

        // Create the URL
        URI url = new URI(baseUrl + port + urlPrefix + "/tournament/" + savedTournament.getId() + "/round");

        // Execute the POST request
        ResponseEntity<String> result = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(round),
                String.class);

        // Assertions
        assertEquals("Round added successfully to the tournament", result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    //@Test // Gives same error as above
    public void addRound_Failure_TournamentNotFound() throws Exception {

        Round round = new Round();
        round.setRoundNum(1);
        round.setId((long) 134);
        round.setScoreboard(new Scoreboard());
        round.setMatchList(new ArrayList<>());

        URI url = new URI(baseUrl + port + urlPrefix + "/tournament/204830/round");

        ResponseEntity<Exception> result = restTemplate.postForEntity(url, round, Exception.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

    @Test // Gives same error as above
    public void addRound_Failure_NoRound() throws Exception {

        Round round = new Round();
        round.setRoundNum(1);
        round.setId((long) 134);
        round.setScoreboard(new Scoreboard());
        round.setMatchList(new ArrayList<>());
        URI url = new URI(baseUrl + port + urlPrefix + "/tournament/204830/round");

        ResponseEntity<String> result = restTemplate.postForEntity(url, null, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());

    }

    @Test
    public void getTournamentParticipants_Success() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(0);
        tournament.setNoOfRounds(0);
        tournament.setStatus("Active");
        tournament.setDate("10/20/1203");

        List<Tournament> tournamentList = new ArrayList<>();
        List<User> userList = new ArrayList<>();
        tournamentList.add(tournament);
        userList.add(user1);

        user1.setCurrentTournaments(tournamentList);
        tournament.setParticipants(userList);

        Tournament savedTournament = tournamentRepository.save(tournament);
        userRepository.save(user1);

        URI url = new URI(baseUrl + port + urlPrefix + "/" + savedTournament.getId() + "/participant");

        ResponseEntity<List<UserDTO>> result = restTemplate.exchange(url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<UserDTO>>() {
                });

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("testUser1", result.getBody().get(0).getUsername());
    }

    @Test
    public void getTournamentParticipants_Failure() throws Exception {
        long tId = (long) 110;

        URI url = new URI(baseUrl + port + urlPrefix + "/" + tId + "/participant");

        ResponseEntity<List<UserDTO>> result = restTemplate.exchange(url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<UserDTO>>() {
                });

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    public void updateTournamentParticipant_Success() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(5);
        tournament.setNoOfRounds(0);
        tournament.setStatus("Active");
        tournament.setDate("10/20/1203");

        List<Tournament> tournamentList = new ArrayList<>();
        List<User> userList = new ArrayList<>();
        tournament.setParticipants(userList);
        user1.setCurrentTournaments(tournamentList);

        User savedUser = userRepository.save(user1);
        Tournament savedTournament = tournamentRepository.save(tournament);

        URI url = new URI(baseUrl + port + urlPrefix + "/" + savedTournament.getId() + "/participant/add");
        String urlTemplate = UriComponentsBuilder.fromUri(url)
                .queryParam("user_id", "{user_id}")
                .encode()
                .toUriString();

        Map<String, Long> params = new HashMap<>();
        params.put("user_id", savedUser.getId());

        ResponseEntity<TournamentDTO> result = restTemplate.exchange(
                urlTemplate,
                HttpMethod.PUT,
                null,
                TournamentDTO.class,
                params);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("testUser1", result.getBody().getParticipants().get(0).getUsername());
    }

    @Test
    public void updateTournamentParticipant_Failure() throws Exception {

        URI url = new URI(baseUrl + port + urlPrefix + "/234/participant/add");
        String urlTemplate = UriComponentsBuilder.fromUri(url)
                .queryParam("user_id", "{user_id}")
                .encode()
                .toUriString();

        Map<String, Long> params = new HashMap<>();
        params.put("user_id", (long) 600);

        ResponseEntity<TournamentDTO> result = restTemplate.exchange(
                urlTemplate,
                HttpMethod.PUT,
                null,
                TournamentDTO.class,
                params);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void getTournamentWithNoCurrentUser_Success() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(0);
        tournament.setNoOfRounds(0);
        tournament.setStatus("Active");
        tournament.setDate("10/20/1203");
        tournamentRepository.save(tournament);

        URI url = new URI(baseUrl + port + urlPrefix + "/tournaments/" + user1.getId());

        ResponseEntity<List<TournamentDTO>> result = restTemplate.exchange(url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TournamentDTO>>() {
                });

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("testTournament", result.getBody().get(0).getTournamentName());
    }

    @Test
    public void getTournamentWithNoCurrentUser_Failure() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(0);
        tournament.setNoOfRounds(0);
        tournament.setStatus("Active");
        tournament.setDate("10/20/1203");
        tournamentRepository.save(tournament);

        URI url = new URI(baseUrl + port + urlPrefix + "/tournaments/12343");

        ResponseEntity<List<TournamentDTO>> result = restTemplate.exchange(url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TournamentDTO>>() {
                });

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void getUsersWithNoCurrentTournament_Success() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(2);
        tournament.setCurrentSize(1);
        tournament.setNoOfRounds(0);
        tournament.setStatus("Active");
        tournament.setDate("10/20/1203");

        List<User> userList = new ArrayList<>();
        userList.add(user1);
        tournament.setParticipants(userList);
        Tournament savedTournament = tournamentRepository.save(tournament);

        List<Tournament> tournamentList = user1.getCurrentTournaments();
        tournamentList.add(tournament);
        userRepository.save(user1);

        URI url = new URI(baseUrl + port + urlPrefix + "/users/" + savedTournament.getId());

        ResponseEntity<List<UserDTO>> result = restTemplate.exchange(url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<UserDTO>>() {
                });

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        assertEquals("testUser2", result.getBody().get(0).getUsername());
    }

    @Test
    public void getUsersWithNoCurrentTournament_Failure_InvalidUId() throws Exception {

        URI url = new URI(baseUrl + port + urlPrefix + "/users/sfew");

        ResponseEntity<Exception> result = restTemplate.exchange(url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Exception>() {
                });

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void getUsersWithNoCurrentTournament_Failure_NoUsers() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(0);
        tournament.setNoOfRounds(0);
        tournament.setStatus("Active");
        tournament.setDate("10/20/1203");
        tournament.setParticipants(new ArrayList<>());
        Tournament saveTournament = tournamentRepository.save(tournament);

        URI url = new URI(baseUrl + port + urlPrefix + "/tournaments/" + saveTournament.getId());

        ResponseEntity<List<UserDTO>> result = restTemplate.exchange(url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<UserDTO>>() {
                });

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void removeParticipant_Success() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(0);
        tournament.setNoOfRounds(0);
        tournament.setStatus("Active");
        tournament.setDate("10/20/1203");

        List<Tournament> tournamentList = new ArrayList<>();
        List<User> userList = new ArrayList<>();

        Tournament savedTournament = tournamentRepository.save(tournament);

        tournamentList.add(savedTournament);
        userList.add(user1);

        user1.setCurrentTournaments(tournamentList);
        savedTournament.setParticipants(userList);

        savedTournament = tournamentRepository.save(savedTournament);
        userRepository.save(user1);

        URI url = new URI(baseUrl + port + urlPrefix + "/" + savedTournament.getId() + "/participant/delete");
        String urlTemplate = UriComponentsBuilder.fromUri(url)
                .queryParam("user_id", "{user_id}")
                .encode()
                .toUriString();

        Map<String, Long> params = new HashMap<>();
        params.put("user_id", user1.getId());

        ResponseEntity<TournamentDTO> result = restTemplate.exchange(
                urlTemplate,
                HttpMethod.PUT,
                null,
                TournamentDTO.class,
                params);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertIterableEquals(new ArrayList<UserDTO>(), result.getBody().getParticipants());
    }

    @Test
    public void removeParticipant_Failure_TournamentNotFound() throws Exception {
        URI url = new URI(baseUrl + port + urlPrefix + "/132/participant/delete");
        String urlTemplate = UriComponentsBuilder.fromUri(url)
                .queryParam("user_id", "{user_id}")
                .encode()
                .toUriString();

        Map<String, Long> params = new HashMap<>();
        params.put("user_id", (long) 13028);

        ResponseEntity<TournamentDTO> result = restTemplate.exchange(
                urlTemplate,
                HttpMethod.PUT,
                null,
                TournamentDTO.class,
                params);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void deleteTournament_Success_TournamentDeleted() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(0);
        tournament.setNoOfRounds(0);
        tournament.setStatus("Active");
        tournament.setDate("10/20/1203");
        Tournament savedTournament = tournamentRepository.save(tournament);

        URI url = new URI(baseUrl + port + urlPrefix + "/tournament/" + savedTournament.getId());

        ResponseEntity<String> result = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                null,
                String.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Tournament with ID " + savedTournament.getId() + " has been deleted.", result.getBody());
        assertEquals(0, tournamentRepository.count());
    }

    @Test
    public void deleteTournament_Failure_TournamentNotFound() throws Exception {
        URI url = new URI(baseUrl + port + urlPrefix + "/tournament/-12480");

        ResponseEntity<String> result = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                null,
                String.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("An error occurred while deleting the tournament.", result.getBody());
    }

    @Test
    public void updateTournament_Success() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(0);
        tournament.setNoOfRounds(0);
        tournament.setStatus("Active");
        tournament.setDate("10/20/1203");
        Tournament savedTournament = tournamentRepository.save(tournament);

        CreateTournamentRequest updateTournament = new CreateTournamentRequest();
        updateTournament.setTournament_name("newTournament");

        URI url = new URI(baseUrl + port + urlPrefix + "/" + savedTournament.getId());

        ResponseEntity<TournamentDTO> result = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                new HttpEntity<>(updateTournament),
                TournamentDTO.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("newTournament", result.getBody().getTournamentName());
    }

    @Test
    public void updateTournament_Failure() throws Exception {
        CreateTournamentRequest updateTournament = new CreateTournamentRequest();
        updateTournament.setTournament_name("newTournament");

        URI url = new URI(baseUrl + port + urlPrefix + "/1408402");

        ResponseEntity<TournamentDTO> result = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                new HttpEntity<>(updateTournament),
                TournamentDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void startTournament_Success() throws Exception {
        Tournament testTournament = new Tournament();
        String tournamentStatus = "active";
        int tournamentSize = 2;

        testTournament.setTournament_name("testTournament");
        testTournament.setSize(tournamentSize);
        testTournament.setCurrentSize(tournamentSize);
        testTournament.setNoOfRounds(1);
        testTournament.setStatus(tournamentStatus);
        testTournament.setDate("10/20/1203");

        List<Tournament> tournamentList = new ArrayList<>();
        List<User> userList = new ArrayList<>();
        tournamentList.add(testTournament);
        userList.add(user1);
        userList.add(user2);

        testTournament.setRounds(new ArrayList<Round>());
        user1.setCurrentTournaments(tournamentList);
        user2.setCurrentTournaments(tournamentList);
        testTournament.setParticipants(userList);

        Tournament savedTournament = tournamentRepository.save(testTournament);
        userRepository.save(user1);
        userRepository.save(user2);

        URI url = new URI(baseUrl + port + urlPrefix + "/" + savedTournament.getId() + "/start");

        ResponseEntity<TournamentDTO> result = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                null,
                TournamentDTO.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("ongoing", result.getBody().getStatus());
        
    }

    @Test
    void startTournament_Failure_CannotFindTournament() throws Exception {
        URI url = new URI(baseUrl + port + urlPrefix + "/404/start");

        ResponseEntity<TournamentDTO> result = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                null,
                TournamentDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void startTournamentService_Success() throws Exception {
        Tournament testTournament = new Tournament();
        String tournamentStatus = "active";
        int tournamentSize = 2;

        testTournament.setTournament_name("testTournament");
        testTournament.setSize(tournamentSize);
        testTournament.setCurrentSize(tournamentSize);
        testTournament.setNoOfRounds(1);
        testTournament.setStatus(tournamentStatus);
        testTournament.setDate("10/20/1203");

        List<Tournament> tournamentList = new ArrayList<>();
        List<User> userList = new ArrayList<>();
        tournamentList.add(testTournament);
        userList.add(user1);
        userList.add(user2);

        user1.setCurrentTournaments(tournamentList);
        user2.setCurrentTournaments(tournamentList);
        testTournament.setParticipants(userList);

        Tournament savedTournament = tournamentRepository.save(testTournament);
        userRepository.save(user1);
        userRepository.save(user2);

        URI url = new URI(baseUrl + port + urlPrefix + "/tournament/" + savedTournament.getId()
                + "/start");

        ResponseEntity<TournamentStartDTO> result = restTemplate.getForEntity(url, TournamentStartDTO.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        TournamentStartDTO tournamentStartDTO = result.getBody();
        
        assertEquals(savedTournament.getTournament_name(), tournamentStartDTO.getTournamentName());
        assertEquals(savedTournament.getStatus(), tournamentStartDTO.getStatus());
        assertEquals(savedTournament.getSize(), tournamentStartDTO.getSize());
        assertEquals(savedTournament.getNoOfRounds(), tournamentStartDTO.getNoOfRounds());
        assertEquals(savedTournament.getId(), tournamentStartDTO.getId());
        assertEquals(savedTournament.getDate(), tournamentStartDTO.getDate());
        assertEquals(savedTournament.getCurrentSize(), tournamentStartDTO.getCurrentSize());
        assertEquals(savedTournament.getCurrentRound(), tournamentStartDTO.getCurrentRound());
        assertIterableEquals(savedTournament.getRounds(), tournamentStartDTO.getRounds());
        assertEquals(savedTournament.getParticipants().size(), tournamentStartDTO.getParticipants().size());
        assertEquals(savedTournament.getParticipants().get(0).getUsername(),
                tournamentStartDTO.getParticipants().get(0).getUsername());
    }

    @Test
    void startTournamentService_Failure_TournamentNotFound() throws Exception {
        URI url = new URI(baseUrl + port + urlPrefix + "/tournament/" + (long) 5092852
                + "/start");

        ResponseEntity<Exception> result = restTemplate.getForEntity(url, Exception.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

    @Test
    void getAllRounds_Success_NoCurrentRounds() throws Exception {
        Tournament testTournament = new Tournament();
        String tournamentStatus = "active";
        int tournamentSize = 2;

        testTournament.setTournament_name("testTournament");
        testTournament.setSize(tournamentSize);
        testTournament.setCurrentSize(tournamentSize);
        testTournament.setNoOfRounds(1);
        testTournament.setStatus(tournamentStatus);
        testTournament.setDate("10/20/1203");

        List<Tournament> tournamentList = new ArrayList<>();
        List<User> userList = new ArrayList<>();
        tournamentList.add(testTournament);
        userList.add(user1);
        userList.add(user2);

        user1.setCurrentTournaments(tournamentList);
        user2.setCurrentTournaments(tournamentList);
        testTournament.setParticipants(userList);

        Tournament savedTournament = tournamentRepository.save(testTournament);
        userRepository.save(user1);
        userRepository.save(user2);

        Round testRound = new Round();
        testRound.setMatchList(new ArrayList<>());
        testRound.setRoundNum(1);
        testRound.setTournament(savedTournament);
        testRound.setIsCompleted(false);

        Scoreboard scoreboard=new Scoreboard();
        List<ScoreboardEntry> entries=new ArrayList<>();
        ScoreboardEntry entry1=new ScoreboardEntry(user1.getId(),0.0);
        ScoreboardEntry entry2=new ScoreboardEntry(user2.getId(),0.0);
        entries.add(entry1);
        entries.add(entry2);
        scoreboard.setScoreboardEntries(entries);
        testRound.setScoreboard(scoreboard);

        Round testRound2 = new Round();
        testRound2.setMatchList(new ArrayList<>());
        testRound2.setRoundNum(2);
        testRound2.setTournament(savedTournament);
        testRound2.setIsCompleted(false);
        testRound2.setScoreboard(scoreboard);

        List<Round> rounds = new ArrayList<>();
        Round savedRound = roundRepository.save(testRound);
        Round savedRound2 = roundRepository.save(testRound2);
        rounds.add(savedRound);
        rounds.add(savedRound2);
        savedTournament.setRounds(rounds);
        tournamentRepository.save(savedTournament);

        URI url = new URI(baseUrl + port + urlPrefix + "/tournament/" + savedTournament.getId()
                + "/start/rounds");

        ResponseEntity<List<RoundDTO>> result = restTemplate.exchange(url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<RoundDTO>>() {
                });

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(2, result.getBody().size());
    }

    @Test
    void getAllRounds_Failure_TournamentNotFound() throws Exception {
        URI url = new URI(baseUrl + port + urlPrefix + "/tournament/" + (long) 123
                + "/start/rounds");

        ResponseEntity<Exception> result = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                Exception.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

    @Test
    void tournamentComplete_Success() throws Exception {
        Tournament testTournament = new Tournament();
        String tournamentStatus = "active";
        int tournamentSize = 2;

        testTournament.setTournament_name("testTournament");
        testTournament.setSize(tournamentSize);
        testTournament.setCurrentSize(tournamentSize);
        testTournament.setNoOfRounds(1);
        testTournament.setStatus(tournamentStatus);
        testTournament.setDate("10/20/1203");

        List<Tournament> tournamentList = new ArrayList<>();
        List<User> userList = new ArrayList<>();
        tournamentList.add(testTournament);
        userList.add(user1);
        userList.add(user2);

        user1.setCurrentTournaments(tournamentList);
        user2.setCurrentTournaments(tournamentList);
        testTournament.setParticipants(userList);

        Tournament savedTournament = tournamentRepository.save(testTournament);
        userRepository.save(user1);
        userRepository.save(user2);

        Round testRound = new Round();
        testRound.setRoundNum(1);
        testRound.setIsCompleted(false);
        testRound.setTournament(savedTournament);
        testRound.setScoreboard(new Scoreboard());

        Round savedRound = roundRepository.save(testRound);

        Match testMatch = new Match();

        testMatch.setPlayer1(user1.getId());
        testMatch.setPlayer2(user2.getId());
        testMatch.setIsComplete(false);
        testMatch.setRound(savedRound);
        savedRound.setMatchList(List.of(testMatch));

        roundRepository.save(savedRound);

        matchRepository.save(testMatch);

        URI url = new URI(baseUrl + port + urlPrefix + "/tournament/" + savedTournament.getId()
                + "/checkComplete");

        ResponseEntity<String> result = restTemplate.getForEntity(url, String.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Successfully checked tournamentService.isComplete", result.getBody());
    }
}
