package com.codewithcled.fullstack_backend_proj1.IntegrationTests;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.codewithcled.fullstack_backend_proj1.DTO.CreateTournamentRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.SignInRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.SignUpRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.TournamentDTO;
import com.codewithcled.fullstack_backend_proj1.DTO.TournamentMapper;
import com.codewithcled.fullstack_backend_proj1.DTO.UserDTO;
import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.repository.TournamentRepository;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;
import com.codewithcled.fullstack_backend_proj1.response.AuthResponse;
import com.codewithcled.fullstack_backend_proj1.controller.AdminController;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AdminControllerIntegrationTest {

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

    private String adminToken = "adminadmin123";

    private String urlPrefix = "/admin";

    private String JWT;

    @BeforeEach
    public void setUp() throws Exception {
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
        userRepository.deleteAll();
        tournamentRepository.deleteAll();
    }

    @Test
    public void validateAdminToken_Success() throws Exception {
        URI uri = new URI(baseUrl + port + urlPrefix + "/signin/validate-admin-token");
        AdminController.TokenRequest tokenRequest = new AdminController.TokenRequest();
        tokenRequest.setToken(adminToken);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + JWT);

        // ResponseEntity<?> result = restTemplate.withBasicAuth("AdminUser", "Admin")
        // .postForEntity(uri, tokenRequest, null);
        ResponseEntity<?> result = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                new HttpEntity<>(tokenRequest, headers),
                // new HttpEntity<>(tokenRequest),
                AdminController.TokenRequest.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void validateAdminToken_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + urlPrefix + "/signin/validate-admin-token");
        AdminController.TokenRequest tokenRequest = new AdminController.TokenRequest();
        tokenRequest.setToken("fa");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + JWT);

        // ResponseEntity<?> result = restTemplate.withBasicAuth("AdminUser", "Admin")
        // .postForEntity(uri, tokenRequest, null);
        ResponseEntity<?> result = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                new HttpEntity<>(tokenRequest, headers),
                AdminController.TokenRequest.class);

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
    }

    @Test
    public void createdTournament_Success() throws Exception {
        URI uri = new URI(baseUrl + port + urlPrefix + "/tournament");
        CreateTournamentRequest createTournamentRequest = new CreateTournamentRequest();
        createTournamentRequest.setCurrentSize(0);
        createTournamentRequest.setDate("10/10/2023");
        createTournamentRequest.setNoOfRounds(4);
        createTournamentRequest.setSize(0);
        createTournamentRequest.setStatus("Active");
        createTournamentRequest.setTournament_name("TestTournament");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + JWT);

        ResponseEntity<TournamentDTO> result = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                new HttpEntity<>(createTournamentRequest, headers),
                TournamentDTO.class);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("TestTournament", result.getBody().getTournamentName());

    }

    @Test
    public void createdTournament_Failure_NoBody() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + JWT);
        URI uri = new URI(baseUrl + port + urlPrefix + "/tournament");

        ResponseEntity<TournamentDTO> result = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                new HttpEntity<>(null, headers),
                TournamentDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void createdTournament_Failure_NoAuthentication() throws Exception {
        CreateTournamentRequest createTournamentRequest = new CreateTournamentRequest();
        createTournamentRequest.setCurrentSize(0);
        createTournamentRequest.setDate("10/10/2023");
        createTournamentRequest.setNoOfRounds(4);
        createTournamentRequest.setSize(0);
        createTournamentRequest.setStatus("Active");
        createTournamentRequest.setTournament_name("TestTournament");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer ");
        URI uri = new URI(baseUrl + port + urlPrefix + "/tournament");

        ResponseEntity<TournamentDTO> result = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                new HttpEntity<>(createTournamentRequest, headers),
                TournamentDTO.class);

        assertEquals(HttpStatus.FOUND, result.getStatusCode());
    }

    @Test
    public void createUser_Success() throws Exception {
        URI uri = new URI(baseUrl + port + urlPrefix + "/signup/user");
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setElo((double) 100);
        signUpRequest.setEmail("TestUser");
        signUpRequest.setPassword(passwordEncoder.encode("TestUser"));
        signUpRequest.setRole("ROLE_USER");
        signUpRequest.setUsername("TestUser");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + JWT);

        ResponseEntity<AuthResponse> result = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                new HttpEntity<>(signUpRequest, headers),
                AuthResponse.class);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    public void createUser_Failure_SameUserNames() throws Exception {
        URI uri = new URI(baseUrl + port + urlPrefix + "/signup/user");
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setElo((double) 100);
        signUpRequest.setEmail("TestUser");
        signUpRequest.setPassword(passwordEncoder.encode("TestUser"));
        signUpRequest.setRole("ROLE_USER");
        signUpRequest.setUsername("TestUser");

        User originalUser = new User();
        originalUser.setElo((double) 100);
        originalUser.setEmail("TestUser");
        originalUser.setPassword(passwordEncoder.encode("TestUser"));
        originalUser.setRole("ROLE_USER");
        originalUser.setUsername("TestUser");
        userRepository.save(originalUser);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + JWT);

        ResponseEntity<AuthResponse> result = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                new HttpEntity<>(signUpRequest, headers),
                AuthResponse.class);

        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
    }

    @Test
    public void createUser_Failure_NoAuthentication() throws Exception {
        URI uri = new URI(baseUrl + port + urlPrefix + "/signup/user");
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setElo((double) 100);
        signUpRequest.setEmail("TestUser");
        signUpRequest.setPassword(passwordEncoder.encode("TestUser"));
        signUpRequest.setRole("ROLE_USER");
        signUpRequest.setUsername("TestUser");

        User originalUser = new User();
        originalUser.setElo((double) 100);
        originalUser.setEmail("TestUser2");
        originalUser.setPassword(passwordEncoder.encode("TestUser2"));
        originalUser.setRole("ROLE_USER");
        originalUser.setUsername("TestUser2");
        userRepository.save(originalUser);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer ");

        ResponseEntity<AuthResponse> result = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                new HttpEntity<>(signUpRequest, headers),
                AuthResponse.class);

        assertEquals(HttpStatus.FOUND, result.getStatusCode());
    }

    @Test
    public void deleteUser_Success() throws Exception {
        User originalUser = new User();
        originalUser.setElo((double) 100);
        originalUser.setEmail("TestUser");
        originalUser.setPassword(passwordEncoder.encode("TestUser"));
        originalUser.setRole("ROLE_USER");
        originalUser.setUsername("TestUser");
        User savedUser = userRepository.save(originalUser);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + JWT);

        URI uri = new URI(baseUrl + port + urlPrefix + "/" + savedUser.getId());

        ResponseEntity<Void> result = restTemplate.exchange(
                uri,
                HttpMethod.DELETE,
                new HttpEntity<>("", headers),
                void.class);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }

    @Test
    public void deleteUser_Failure_UserNotFound() throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + JWT);
        URI uri = new URI(baseUrl + port + urlPrefix + "/" + (long) 110);

        ResponseEntity<Void> result = restTemplate.exchange(
                uri,
                HttpMethod.DELETE,
                new HttpEntity<>("", headers),
                void.class);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    public void deleteUser_Failure_NotAuthenticated() throws Exception {
        User originalUser = new User();
        originalUser.setElo((double) 100);
        originalUser.setEmail("TestUser");
        originalUser.setPassword(passwordEncoder.encode("TestUser"));
        originalUser.setRole("ROLE_USER");
        originalUser.setUsername("TestUser");

        User savedUser = userRepository.save(originalUser);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer ");
        URI uri = new URI(baseUrl + port + urlPrefix + "/" + savedUser.getId());

        ResponseEntity<Void> result = restTemplate.exchange(
                uri,
                HttpMethod.DELETE,
                new HttpEntity<>("", headers),
                void.class);

        assertEquals(HttpStatus.FOUND, result.getStatusCode());
    }

    @Test
    public void getUserByUsername_Success() throws Exception {
        User originalUser = new User();
        originalUser.setElo((double) 100);
        originalUser.setEmail("TestUser");
        originalUser.setPassword(passwordEncoder.encode("TestUser"));
        originalUser.setRole("ROLE_USER");
        originalUser.setUsername("TestUser");
        userRepository.save(originalUser);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + JWT);

        URI uri = new URI(baseUrl + port + urlPrefix + "/TestUser");

        ResponseEntity<UserDTO> result = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                new HttpEntity<>("", headers),
                UserDTO.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void getUserByUsername_Failure_userNotFound() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + JWT);
        URI uri = new URI(baseUrl + port + urlPrefix + "/TestUser");

        ResponseEntity<UserDTO> result = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                new HttpEntity<>("", headers),
                UserDTO.class);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    public void getUserByUsername_Failure_NotAuthorized() throws Exception {
        User originalUser = new User();
        originalUser.setElo((double) 100);
        originalUser.setEmail("TestUser");
        originalUser.setPassword(passwordEncoder.encode("TestUser"));
        originalUser.setRole("ROLE_USER");
        originalUser.setUsername("TestUser");
        userRepository.save(originalUser);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer ");
        URI uri = new URI(baseUrl + port + urlPrefix + "/TestUser");

        ResponseEntity<UserDTO> result = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                new HttpEntity<>("", headers),
                UserDTO.class);

        assertEquals(HttpStatus.FOUND, result.getStatusCode());
    }

    @Test
    public void deleteTournament_Success() throws Exception {

        Tournament originalTournament = new Tournament();
        originalTournament.setSize(0);
        originalTournament.setDate("20/10/2002");
        originalTournament.setNoOfRounds(1);
        originalTournament.setTournament_name("TestUser");
        Tournament savedTournament = tournamentRepository.save(originalTournament);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + JWT);
        URI uri = new URI(baseUrl + port + urlPrefix + "/tournament/" + savedTournament.getId());

        ResponseEntity<String> result = restTemplate.exchange(
                uri,
                HttpMethod.DELETE,
                new HttpEntity<>("", headers),
                String.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Tournament with ID " + savedTournament.getId() + " has been deleted.", result.getBody());
        assertEquals(0, tournamentRepository.count());
    }

    @Test // returns OK instead of throwing exception
    public void deleteTournament_Failure_TournamentNotFound() throws Exception {
        URI uri = new URI(baseUrl + port + urlPrefix + "/tournament/" + 110);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + JWT);

        ResponseEntity<String> result = restTemplate.exchange(
                uri,
                HttpMethod.DELETE,
                new HttpEntity<>("", headers),
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Tournament with ID " + 110 + " not found.", result.getBody());
    }

    @Test
    public void deleteTournament_Failure_NotAuthorized() throws Exception {
        URI uri = new URI(baseUrl + port + urlPrefix + "/tournament/" + 110);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer ");

        ResponseEntity<String> result = restTemplate.exchange(
                uri,
                HttpMethod.DELETE,
                new HttpEntity<>("", headers),
                String.class);

        assertEquals(HttpStatus.FOUND, result.getStatusCode());
    }

    @Test
    public void updateUser_Success() throws Exception {
        User originalUser = new User();
        originalUser.setElo((double) 100);
        originalUser.setEmail("TestUser");
        originalUser.setPassword(passwordEncoder.encode("TestUser"));
        originalUser.setRole("ROLE_USER");
        originalUser.setUsername("TestUser");
        User savedUser = userRepository.save(originalUser);

        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setElo((double) 110);
        signUpRequest.setEmail("UpdatedUser");
        signUpRequest.setPassword(passwordEncoder.encode("UpdateUser"));
        signUpRequest.setRole("ROLE_Admin");
        signUpRequest.setUsername("UpdatedUser");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + JWT);
        URI uri = new URI(baseUrl + port + urlPrefix + "/user/" + savedUser.getId());
        HttpEntity<SignUpRequest> updateUser = new HttpEntity<>(signUpRequest, headers);

        ResponseEntity<UserDTO> result = restTemplate.withBasicAuth("AdminUser", "Admin")
                .exchange(uri, HttpMethod.PUT, updateUser, UserDTO.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());

        UserDTO resultingDTO = result.getBody();
        assertEquals(signUpRequest.getUsername(), resultingDTO.getUsername());
        assertEquals(signUpRequest.getElo(), resultingDTO.getElo());
        assertEquals(signUpRequest.getEmail(), resultingDTO.getEmail());
        assertEquals(signUpRequest.getRole(), resultingDTO.getRole());
    }

    @Test
    public void updateUser_Failure_UserNotFound() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setElo((double) 100);
        signUpRequest.setEmail("TestUser");
        signUpRequest.setPassword(passwordEncoder.encode("TestUser"));
        signUpRequest.setRole("ROLE_USER");
        signUpRequest.setUsername("updatedUser");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + JWT);
        URI uri = new URI(baseUrl + port + urlPrefix + "/user/9");
        HttpEntity<SignUpRequest> updateUser = new HttpEntity<>(signUpRequest, headers);

        ResponseEntity<UserDTO> result = restTemplate.exchange(
                uri,
                HttpMethod.PUT,
                updateUser,
                UserDTO.class);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    public void updateUser_Failure_NotAuthorized() throws Exception {
        User originalUser = new User();
        originalUser.setElo((double) 100);
        originalUser.setEmail("TestUser");
        originalUser.setPassword(passwordEncoder.encode("TestUser"));
        originalUser.setRole("ROLE_USER");
        originalUser.setUsername("TestUser");
        User savedUser = userRepository.save(originalUser);

        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setElo((double) 100);
        signUpRequest.setEmail("TestUser");
        signUpRequest.setPassword(passwordEncoder.encode("TestUser"));
        signUpRequest.setRole("ROLE_USER");
        signUpRequest.setUsername("updatedUser");

        URI uri = new URI(baseUrl + port + urlPrefix + "/user/" + savedUser.getId());
        HttpEntity<SignUpRequest> updateUser = new HttpEntity<>(signUpRequest);

        ResponseEntity<UserDTO> result = restTemplate.exchange(
                uri,
                HttpMethod.PUT,
                updateUser,
                UserDTO.class);

        assertEquals(HttpStatus.FOUND, result.getStatusCode());
    }

    @Test
    public void updateTournament_Success() throws Exception {
        CreateTournamentRequest tournamentUpdateData = new CreateTournamentRequest();
        tournamentUpdateData.setTournament_name("newName");
        tournamentUpdateData.setDate("10/22/2011");
        tournamentUpdateData.setSize(5);
        tournamentUpdateData.setStatus("completed");
        tournamentUpdateData.setCurrentSize(1);
        tournamentUpdateData.setNoOfRounds(4);

        Tournament originalTournament = new Tournament();
        originalTournament.setTournament_name("oldName");
        originalTournament.setDate("11/22/2011");
        originalTournament.setSize(4);
        originalTournament.setStatus("active");
        originalTournament.setNoOfRounds(3);
        Tournament savedTournament = tournamentRepository.save(originalTournament);

        URI uri = new URI(baseUrl + port + urlPrefix + "/tournament/" + savedTournament.getId());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + JWT);

        HttpEntity<CreateTournamentRequest> updateTournament = new HttpEntity<>(tournamentUpdateData, headers);

        ResponseEntity<TournamentDTO> result = restTemplate.exchange(
                uri,
                HttpMethod.PUT,
                updateTournament,
                TournamentDTO.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());

        TournamentDTO resultingDTO = result.getBody();
        assertEquals(tournamentUpdateData.getTournament_name(),resultingDTO.getTournamentName());
        assertEquals(tournamentUpdateData.getDate(),resultingDTO.getDate());
        assertEquals(tournamentUpdateData.getSize(),resultingDTO.getSize());
        assertEquals(tournamentUpdateData.getStatus(),resultingDTO.getStatus());
        assertEquals(tournamentUpdateData.getCurrentSize(),resultingDTO.getCurrentSize());
        assertEquals(tournamentUpdateData.getNoOfRounds(),resultingDTO.getNoOfRounds());
    }

    @Test
    public void updateTournament_Failure_TournamentNotFound() throws Exception {
        CreateTournamentRequest tournamentUpdateData = new CreateTournamentRequest();
        tournamentUpdateData.setTournament_name("newName");
        tournamentUpdateData.setDate("10/22/2011");
        tournamentUpdateData.setSize(5);
        tournamentUpdateData.setStatus("completed");
        tournamentUpdateData.setCurrentSize(1);
        tournamentUpdateData.setNoOfRounds(4);

        URI uri = new URI(baseUrl + port + urlPrefix + "/tournament/132");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + JWT);

        HttpEntity<CreateTournamentRequest> updateTournament = new HttpEntity<>(tournamentUpdateData, headers);

        ResponseEntity<TournamentDTO> result = restTemplate.exchange(
                uri,
                HttpMethod.PUT,
                updateTournament,
                TournamentDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void updateTournament_Failure_NoAuthentication() throws Exception {
        CreateTournamentRequest tournamentUpdateData = new CreateTournamentRequest();
        tournamentUpdateData.setTournament_name("newName");
        tournamentUpdateData.setDate("10/22/2011");
        tournamentUpdateData.setSize(5);
        tournamentUpdateData.setStatus("completed");
        tournamentUpdateData.setCurrentSize(1);
        tournamentUpdateData.setNoOfRounds(4);

        Tournament originalTournament = new Tournament();
        originalTournament.setTournament_name("oldName");
        originalTournament.setDate("11/22/2011");
        originalTournament.setSize(4);
        originalTournament.setStatus("active");
        originalTournament.setNoOfRounds(3);
        Tournament savedTournament = tournamentRepository.save(originalTournament);

        URI uri = new URI(baseUrl + port + urlPrefix + "/tournament/" + savedTournament.getId());

        HttpEntity<CreateTournamentRequest> updateTournament = new HttpEntity<>(tournamentUpdateData);

        ResponseEntity<TournamentDTO> result = restTemplate.exchange(
                uri,
                HttpMethod.PUT,
                updateTournament,
                TournamentDTO.class);

        assertEquals(HttpStatus.FOUND, result.getStatusCode());
    }
}
