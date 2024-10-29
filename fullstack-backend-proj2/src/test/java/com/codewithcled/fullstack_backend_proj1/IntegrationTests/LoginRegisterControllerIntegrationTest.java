package com.codewithcled.fullstack_backend_proj1.IntegrationTests;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.codewithcled.fullstack_backend_proj1.DTO.SignInRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.SignUpRequest;
import com.codewithcled.fullstack_backend_proj1.controller.LoginRegisterController;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.repository.TournamentRepository;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;
import com.codewithcled.fullstack_backend_proj1.response.AuthResponse;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class LoginRegisterControllerIntegrationTest {
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

    private String urlPrefix = "/auth";

    private String adminToken = "adminadmin123";

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
        tournamentRepository.deleteAll();
    }

    @Test
    public void validateAdminToken_Success() throws Exception {
        URI uri = new URI(baseUrl + port + urlPrefix + "/validate-admin-token");

        LoginRegisterController.TokenRequest tokenRequest = new LoginRegisterController.TokenRequest();
        tokenRequest.setToken(adminToken);

        ResponseEntity<?> result = restTemplate.postForEntity(uri, tokenRequest, null);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void validateAdminToken_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + urlPrefix + "/validate-admin-token");

        LoginRegisterController.TokenRequest tokenRequest = new LoginRegisterController.TokenRequest();
        tokenRequest.setToken("sfjoewjoj");

        ResponseEntity<?> result = restTemplate.postForEntity(uri, tokenRequest, null);

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
    }

    @Test
    public void createUserHandler_Success() throws Exception {
        URI uri = new URI(baseUrl + port + urlPrefix + "/signup");

        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setElo((double) 100);
        signUpRequest.setEmail("TestUser");
        signUpRequest.setPassword(passwordEncoder.encode("TestUser"));
        signUpRequest.setRole("ROLE_USER");
        signUpRequest.setUsername("TestUser");

        ResponseEntity<AuthResponse> result = restTemplate.postForEntity(uri, signUpRequest, AuthResponse.class);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    public void createUserHandler_Failure() throws Exception {
        URI uri = new URI(baseUrl + port + urlPrefix + "/signup");

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

        ResponseEntity<AuthResponse> result = restTemplate.postForEntity(uri, signUpRequest, AuthResponse.class);

        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
    }

    @Test
    public void signin_Success() throws Exception{
        URI uri = new URI(baseUrl + port + urlPrefix + "/signin");

        User originalUser = new User();
        originalUser.setElo((double) 100);
        originalUser.setEmail("TestUser");
        originalUser.setPassword(passwordEncoder.encode("TestUser"));
        originalUser.setRole("ROLE_USER");
        originalUser.setUsername("TestUser");
        userRepository.save(originalUser);

        SignInRequest signInRequest=new SignInRequest();
        signInRequest.setUsername("TestUser");
        signInRequest.setPassword("TestUser");

        ResponseEntity<AuthResponse> result=restTemplate.postForEntity(uri, signInRequest, AuthResponse.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void signin_Failure_NoUser() throws Exception{//Will throw exception at loadUserByUsername
        URI uri = new URI(baseUrl + port + urlPrefix + "/signin");

        SignInRequest signInRequest=new SignInRequest();

        ResponseEntity<AuthResponse> result=restTemplate.postForEntity(uri, signInRequest, AuthResponse.class);

        assertEquals(HttpStatus.FOUND, result.getStatusCode());
    }

    @Test
    public void signin_Failure_WrongPassword() throws Exception{
        URI uri = new URI(baseUrl + port + urlPrefix + "/signin");

        User originalUser = new User();
        originalUser.setElo((double) 100);
        originalUser.setEmail("TestUser");
        originalUser.setPassword(passwordEncoder.encode("TestUser"));
        originalUser.setRole("ROLE_USER");
        originalUser.setUsername("TestUser");
        userRepository.save(originalUser);

        SignInRequest signInRequest=new SignInRequest();
        signInRequest.setUsername("TestUser");
        signInRequest.setPassword("");

        ResponseEntity<AuthResponse> result=restTemplate.postForEntity(uri, signInRequest, AuthResponse.class);

        assertEquals(HttpStatus.FOUND, result.getStatusCode());
    }
}
