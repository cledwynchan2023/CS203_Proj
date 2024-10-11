package com.codewithcled.fullstack_backend_proj1;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.catalina.connector.Response;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import com.codewithcled.fullstack_backend_proj1.DTO.CreateTournamentRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.TournamentDTO;
import com.codewithcled.fullstack_backend_proj1.DTO.TournamentMapper;
import com.codewithcled.fullstack_backend_proj1.DTO.UserDTO;
import com.codewithcled.fullstack_backend_proj1.DTO.UserMapper;
import com.codewithcled.fullstack_backend_proj1.model.Round;
import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.model.User;
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
    private TestRestTemplate restTemplate;

    private String urlPrefix = "/t";

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
        tournamentRepository.deleteAll();
    }

    @Test
    public void getAllTournaments_Success() throws Exception{
        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(0);
        tournament.setNoOfRounds(0);
        tournament.setActive("Active");
        tournament.setDate("10/20/1203");
        tournamentRepository.save(tournament);

        URI url = new URI(baseUrl + port + urlPrefix + "/tournaments");

        ResponseEntity<List<TournamentDTO>> result = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<TournamentDTO>>() {});

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void getAllTournaments_Failure() throws Exception{
        URI url = new URI(baseUrl + port + urlPrefix + "/tournaments");

        ResponseEntity<List<TournamentDTO>> result = restTemplate.exchange(url, 
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<TournamentDTO>>() {});

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }

    @Test
    public void getTournamentById_Success() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(0);
        tournament.setNoOfRounds(0);
        tournament.setDate("10/20/1203");
        tournament.setActive("Active");
        Tournament savedTournament = tournamentRepository.save(tournament);

        URI url = new URI(baseUrl + port + urlPrefix + "/" + savedTournament.getId());

        ResponseEntity<TournamentDTO> result = restTemplate.getForEntity(url, TournamentDTO.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void getTournamentById_Failure() throws Exception {

        URI url = new URI(baseUrl + port + urlPrefix + "/2423");

        ResponseEntity<TournamentDTO> result = restTemplate.getForEntity(url, TournamentDTO.class);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    public void addRound_Success() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(0);
        tournament.setNoOfRounds(0);
        tournament.setDate("10/20/1203");
        tournament.setActive("Active");
        Tournament savedTournament = tournamentRepository.save(tournament);

        Round round = new Round();
        round.setRoundNum(1);

        URI url = new URI(baseUrl + port + urlPrefix + "/tournament/" + savedTournament.getId() + "/round");

        ResponseEntity<String> result = restTemplate.postForEntity(url, round, String.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Round added successfully to the tournament", result.getBody());
    }

    @Test
    public void addRound_Failure() {

        Round round = new Round();
        round.setRoundNum(1);

        try {

            URI url = new URI(baseUrl + port + urlPrefix + "/tournament/204830/round");

            restTemplate.postForEntity(url, round, String.class);

        } catch (Exception e) {
            assertEquals("Tournament not found", e.getMessage());
        }

    }

    @Test
    public void getTournamentParticipants_Success() throws Exception {
        User user = new User();
        user.setUsername("testUser");
        user.setRole("ROLE_USER");
        user.setEmail("testUser");

        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(0);
        tournament.setNoOfRounds(0);
        tournament.setActive("Active");
        tournament.setDate("10/20/1203");

        List<Tournament> tournamentList = new ArrayList<>();
        List<User> userList = new ArrayList<>();
        tournamentList.add(tournament);
        userList.add(user);

        user.setCurrentTournaments(tournamentList);
        tournament.setParticipants(userList);

        Tournament savedTournament = tournamentRepository.save(tournament);
        userRepository.save(user);

        URI url = new URI(baseUrl + port + urlPrefix +"/"+ savedTournament.getId() + "/participant");

        ResponseEntity<List<UserDTO>> result = restTemplate.exchange(url, 
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<UserDTO>>() {});

        assertEquals(HttpStatus.OK,result.getStatusCode());
    }

    @Test
    public void getTournamentParticipants_Failure() throws Exception {
        long tId = (long) 110;

        URI url = new URI(baseUrl + port + urlPrefix +"/"+ tId + "/participant");

        ResponseEntity<List<UserDTO>> result = restTemplate.exchange(url, 
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<UserDTO>>() {});

        assertEquals(HttpStatus.NOT_FOUND,result.getStatusCode());
    }

    @Test
    public void updateTournamentParticipant_Success() throws Exception{
        User user = new User();
        user.setUsername("testUser");
        user.setRole("ROLE_USER");
        user.setEmail("testUser");

        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(0);
        tournament.setNoOfRounds(0);
        tournament.setActive("Active");
        tournament.setDate("10/20/1203");

        List<Tournament> tournamentList = new ArrayList<>();
        List<User> userList = new ArrayList<>();
        tournamentList.add(tournament);
        userList.add(user);

        User savedUser = userRepository.save(user);
        Tournament savedTournament = tournamentRepository.save(tournament);


        URI url = new URI(baseUrl + port + urlPrefix +"/"+ savedTournament.getId() + "/participant/add");
        String urlTemplate=UriComponentsBuilder.fromUri(url)
        .queryParam("user_id", "{user_id}")
        .encode()
        .toUriString();
        
        Map<String, Long> params = new HashMap<>();
        params.put("user_id",savedUser.getId());

        ResponseEntity<TournamentDTO> result=restTemplate.exchange(urlTemplate, HttpMethod.PUT, null, TournamentDTO.class, params);

        assertEquals(HttpStatus.OK,result.getStatusCode());
    }

    @Test
    public void updateTournamentParticipant_Failure() throws Exception{

        URI url = new URI(baseUrl + port + urlPrefix + "/234/participant/add");
        String urlTemplate=UriComponentsBuilder.fromUri(url)
        .queryParam("user_id", "{user_id}")
        .encode()
        .toUriString();
        
        Map<String, Long> params = new HashMap<>();
        params.put("user_id",(long)600);

        ResponseEntity<TournamentDTO> result=restTemplate.exchange(urlTemplate, HttpMethod.PUT, null, TournamentDTO.class, params);

        assertEquals(HttpStatus.BAD_REQUEST,result.getStatusCode());
    }

    @Test
    public void getTournamentWithNoCurrentUser_Success() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(0);
        tournament.setNoOfRounds(0);
        tournament.setActive("Active");
        tournament.setDate("10/20/1203");
        tournamentRepository.save(tournament);

        User user = new User();
        user.setUsername("testUser");
        user.setRole("ROLE_USER");
        user.setEmail("testUser");
        User savedUser = userRepository.save(user);

        URI url = new URI(baseUrl + port + urlPrefix + "/tournaments/" + savedUser.getId());

        ResponseEntity<List<TournamentDTO>> result = restTemplate.exchange(url, 
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<TournamentDTO>>() {});

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void getTournamentWithNoCurrentUser_Failure() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(0);
        tournament.setNoOfRounds(0);
        tournament.setActive("Active");
        tournament.setDate("10/20/1203");
        tournamentRepository.save(tournament);

        URI url = new URI(baseUrl + port + urlPrefix + "/tournaments/12343");

        ResponseEntity<List<TournamentDTO>> result = restTemplate.exchange(url, 
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<TournamentDTO>>() {});

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void getUsersWithNoCurrentTournament_Success() throws Exception {
        User user = new User();
        user.setUsername("testUser");
        user.setRole("ROLE_USER");
        user.setEmail("testUser");
        user.setCurrentTournaments(new ArrayList<>());
        userRepository.save(user);

        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(0);
        tournament.setNoOfRounds(0);
        tournament.setActive("Active");
        tournament.setDate("10/20/1203");
        tournament.setParticipants(new ArrayList<>());
        Tournament saveTournament = tournamentRepository.save(tournament);

        URI url = new URI(baseUrl + port + urlPrefix + "/tournaments/" + saveTournament.getId());

        ResponseEntity<List<UserDTO>> result = restTemplate.exchange(url, 
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<UserDTO>>() {});

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void getUsersWithNoCurrentTournament_Failure() throws Exception {

        URI url = new URI(baseUrl + port + urlPrefix + "/tournaments/12380");

        ResponseEntity<List<UserDTO>> result = restTemplate.exchange(url, 
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<UserDTO>>() {});

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void getUsersWithNoCurrentTournament_FailureNoUsers() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(0);
        tournament.setNoOfRounds(0);
        tournament.setActive("Active");
        tournament.setDate("10/20/1203");
        tournament.setParticipants(new ArrayList<>());
        Tournament saveTournament = tournamentRepository.save(tournament);

        URI url = new URI(baseUrl + port + urlPrefix + "/tournaments/"+saveTournament.getId());

        ResponseEntity<List<UserDTO>> result = restTemplate.exchange(url, 
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<UserDTO>>() {});

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void removeParticipant_Success() throws Exception{
        User user = new User();
        user.setUsername("testUser");
        user.setRole("ROLE_USER");
        user.setEmail("testUser");

        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(0);
        tournament.setNoOfRounds(0);
        tournament.setActive("Active");
        tournament.setDate("10/20/1203");

        List<Tournament> tournamentList = new ArrayList<>();
        List<User> userList = new ArrayList<>();
        tournamentList.add(tournament);
        userList.add(user);

        user.setCurrentTournaments(tournamentList);
        tournament.setParticipants(userList);

        
        Tournament savedTournament = tournamentRepository.save(tournament);
        User savedUser = userRepository.save(user);

        URI url = new URI(baseUrl + port + urlPrefix +"/"+ savedTournament.getId() + "/participant/delete");
        String urlTemplate=UriComponentsBuilder.fromUri(url)
        .queryParam("user_id", "{user_id}")
        .encode()
        .toUriString();
        
        Map<String, Long> params = new HashMap<>();
        params.put("user_id",savedUser.getId());

        ResponseEntity<TournamentDTO> result=restTemplate.exchange(urlTemplate, HttpMethod.PUT, null, TournamentDTO.class, params);

        assertEquals(HttpStatus.OK,result.getStatusCode());
    }

    @Test
    public void removeParticipant_Failure() throws Exception{
        URI url = new URI(baseUrl + port + urlPrefix +"/132/participant/delete");
        String urlTemplate=UriComponentsBuilder.fromUri(url)
        .queryParam("user_id", "{user_id}")
        .encode()
        .toUriString();
        
        Map<String, Long> params = new HashMap<>();
        params.put("user_id",(long)13028);

        ResponseEntity<TournamentDTO> result=restTemplate.exchange(urlTemplate, HttpMethod.PUT, null, TournamentDTO.class, params);

        assertEquals(HttpStatus.BAD_REQUEST,result.getStatusCode());
    }

    @Test
    public void deleteTournament_Success() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(0);
        tournament.setNoOfRounds(0);
        tournament.setActive("Active");
        tournament.setDate("10/20/1203");
        Tournament savedTournament = tournamentRepository.save(tournament);

        URI url = new URI(baseUrl + port + urlPrefix + "/tournament/" + savedTournament.getId());

        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.DELETE, null, String.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Tournament with ID " + savedTournament.getId() + " has been deleted.", result.getBody());
    }

    @Test
    public void deleteTournament_Failure() throws Exception {
        URI url = new URI(baseUrl + port + urlPrefix + "/tournament/1120483");

        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.DELETE, null, String.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("An error occurred while deleting the tournament.", result.getBody());
    }

    
    @Test
    public void updateTournament_Success() throws Exception{
        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(0);
        tournament.setNoOfRounds(0);
        tournament.setActive("Active");
        tournament.setDate("10/20/1203");
        Tournament savedTournament=tournamentRepository.save(tournament);

        CreateTournamentRequest updateTournament=new CreateTournamentRequest();
        updateTournament.setTournament_name("newTournament");

        URI url = new URI(baseUrl + port + urlPrefix +"/"+savedTournament.getId());

        ResponseEntity<TournamentDTO> result=restTemplate.exchange(
            url, 
            HttpMethod.PUT,
            new HttpEntity<>(updateTournament), 
            TournamentDTO.class);

        assertEquals(HttpStatus.OK,result.getStatusCode());
    }

    @Test
    public void updateTournament_Failure() throws Exception{
        CreateTournamentRequest updateTournament=new CreateTournamentRequest();
        updateTournament.setTournament_name("newTournament");

        URI url = new URI(baseUrl + port + urlPrefix +"/1408402");

        ResponseEntity<TournamentDTO> result=restTemplate.exchange(
            url, 
            HttpMethod.PUT,
            new HttpEntity<>(updateTournament), 
            TournamentDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST,result.getStatusCode());
    }

}
