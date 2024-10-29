package com.codewithcled.fullstack_backend_proj1.IntegrationTests;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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

import com.codewithcled.fullstack_backend_proj1.DTO.EditUserRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.SignUpRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.TournamentDTO;
import com.codewithcled.fullstack_backend_proj1.DTO.UserDTO;
import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.repository.TournamentRepository;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserControllerIntegrationTest {
    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

    @Autowired
    private TournamentRepository tournamentRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String urlPrefix = "/u";

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
        tournamentRepository.deleteAll();
    }

    @SuppressWarnings("null")
    @Test
    public void getAllUsers_Success() throws Exception{
        User user = new User();
        user.setUsername("testUser");
        user.setRole("ROLE_USER");
        user.setEmail("testUser");
        userRepository.save(user);

        URI url=new URI(baseUrl+port+urlPrefix+"/users");

        ResponseEntity<List<UserDTO>> result=restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<UserDTO>>() {});

        assertEquals(HttpStatus.OK,result.getStatusCode());
        assertEquals("testUser",result.getBody().get(0).getUsername());
    }

    @Test
    public void getAllUsers_Fail() throws Exception{
        URI url=new URI(baseUrl+port+urlPrefix+"/users");

        ResponseEntity<List<UserDTO>> result=restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<UserDTO>>() {});

        assertEquals(HttpStatus.NO_CONTENT,result.getStatusCode());
    }

    @Test
    public void getUserByUsername_Success() throws Exception {
        User user = new User();
        user.setUsername("testUser");
        user.setRole("ROLE_USER");
        user.setEmail("testUser");
        User savedUser = userRepository.save(user);

        URI url = new URI(baseUrl + port + urlPrefix + "/" + savedUser.getUsername());

        ResponseEntity<UserDTO> result = restTemplate.getForEntity(url, UserDTO.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("testUser", result.getBody().getUsername());
    }

    @Test
    public void getUserByUsername_Failure() throws Exception {
        URI url = new URI(baseUrl + port + urlPrefix + "/sf123");

        ResponseEntity<UserDTO> result = restTemplate.getForEntity(url, UserDTO.class);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    public void getUserParticipatingTournaments_Success() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setTournament_name("testTournament");
        tournament.setSize(0);
        tournament.setNoOfRounds(0);
        tournament.setStatus("active");
        tournament.setDate("10/20/1203");

        User user = new User();
        user.setUsername("testUser");
        user.setRole("ROLE_USER");
        user.setEmail("testUser");

        List<User> userList = new ArrayList<>();
        List<Tournament> tournamentList = new ArrayList<>();

        userList.add(user);
        tournamentList.add(tournament);

        user.setCurrentTournaments(tournamentList);
        tournament.setParticipants(userList);

        tournamentRepository.save(tournament);
        User savedUser = userRepository.save(user);

        URI url = new URI(baseUrl + port + urlPrefix + "/" + savedUser.getId() + "/currentTournament");

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
    public void getUserParticipatingTournaments_Failure() throws Exception {
        URI url = new URI(baseUrl + port + urlPrefix + "/2348/currentTournament");

        ResponseEntity<List<TournamentDTO>> result = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TournamentDTO>>() {
                });

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    public void getUserById_Success() throws Exception{
        User user = new User();
        user.setUsername("testUser");
        user.setRole("ROLE_USER");
        user.setEmail("testUser");
        User savedUser = userRepository.save(user);

        URI url = new URI(baseUrl + port + urlPrefix + "/id/"+savedUser.getId());

        ResponseEntity<UserDTO> result = restTemplate.getForEntity(url, UserDTO.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("testUser", result.getBody().getUsername());
    }

    @Test
    public void getUserById_Failure() throws Exception{

        URI url = new URI(baseUrl + port + urlPrefix + "/id/14802");

        ResponseEntity<UserDTO> result = restTemplate.getForEntity(url, UserDTO.class);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    public void deleteUser_Success() throws Exception{
        User user = new User();
        user.setUsername("testUser");
        user.setRole("ROLE_USER");
        user.setEmail("testUser");
        User savedUser = userRepository.save(user);

        URI url = new URI(baseUrl + port + urlPrefix + "/" + savedUser.getId());

        ResponseEntity<Void> result=restTemplate.exchange(
            url, 
            HttpMethod.DELETE,
            null,
            Void.class);

        assertEquals(HttpStatus.NO_CONTENT,result.getStatusCode());
    }

    @Test
    public void deleteUser_Failure() throws Exception{

        URI url = new URI(baseUrl + port + urlPrefix + "/1201382");

        ResponseEntity<Void> result=restTemplate.exchange(
            url, 
            HttpMethod.DELETE,
            null,
            Void.class);

        assertEquals(HttpStatus.NOT_FOUND,result.getStatusCode());
    }

    @Test
    public void updateUser_Success() throws Exception{
        User user = new User();
        user.setUsername("testUser");
        user.setRole("ROLE_USER");
        user.setEmail("testUser");
        user.setPassword(passwordEncoder.encode(baseUrl));
        user.setElo((double)1000);
        User savedUser = userRepository.save(user);

        SignUpRequest signUpRequest=new SignUpRequest();
        signUpRequest.setUsername("newUser");
        signUpRequest.setEmail("newUser");
        signUpRequest.setRole("ROLE_USER");
        signUpRequest.setPassword(passwordEncoder.encode(baseUrl));
        signUpRequest.setElo((double)1000);

        URI url = new URI(baseUrl + port + urlPrefix + "/" + savedUser.getId());

        ResponseEntity<UserDTO> result=restTemplate.exchange(
            url,
            HttpMethod.PUT,
            new HttpEntity<>(signUpRequest),
            UserDTO.class);

        assertEquals(HttpStatus.OK,result.getStatusCode());
        assertEquals("newUser",result.getBody().getUsername());
    }

    @Test
    public void updateUser_Failure() throws Exception{
        SignUpRequest signUpRequest=new SignUpRequest();
        signUpRequest.setUsername("newUser");
        signUpRequest.setEmail("newUser");

        URI url = new URI(baseUrl + port + urlPrefix + "/1380912");

        ResponseEntity<UserDTO> result=restTemplate.exchange(
            url,
            HttpMethod.PUT,
            new HttpEntity<>(signUpRequest),
            UserDTO.class);

        assertEquals(HttpStatus.NOT_FOUND,result.getStatusCode());
    }

    @Test
    public void updateUserWithoutPassword_Success() throws Exception{
        EditUserRequest editUserRequest = new EditUserRequest();
        editUserRequest.setUsername("newUsername");
        editUserRequest.setElo((double)1500);
        editUserRequest.setRole("ROLE_ADMIN");

        User testUser=new User();
        testUser.setUsername("oldUsername");
        testUser.setRole("ROLE_USER");
        testUser.setElo((double)1200);
        User savedUser=userRepository.save(testUser);

        URI url = new URI(baseUrl + port + urlPrefix + "/user/"+savedUser.getId());

        ResponseEntity<UserDTO> result=restTemplate.exchange(
            url,
            HttpMethod.PUT,
            new HttpEntity<>(editUserRequest),
            UserDTO.class);

        assertEquals(HttpStatus.OK,result.getStatusCode());
        assertEquals(editUserRequest.getUsername(),result.getBody().getUsername());
        assertEquals(editUserRequest.getRole(),result.getBody().getRole());
        assertEquals(editUserRequest.getElo(),result.getBody().getElo());
    }

    @Test
    public void updateUserWithoutPassword_Failure_UserNotFound() throws Exception{
        EditUserRequest editUserRequest = new EditUserRequest();
        editUserRequest.setUsername("newUsername");
        editUserRequest.setElo((double)1500);
        editUserRequest.setRole("ROLE_ADMIN");
        URI url = new URI(baseUrl + port + urlPrefix + "/user/1380912");

        ResponseEntity<UserDTO> result=restTemplate.exchange(
            url,
            HttpMethod.PUT,
            new HttpEntity<>(editUserRequest),
            UserDTO.class);

        assertEquals(HttpStatus.NOT_FOUND,result.getStatusCode());
    }

    
}
