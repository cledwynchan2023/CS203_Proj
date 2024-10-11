package com.codewithcled.fullstack_backend_proj1;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.apache.catalina.connector.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;

import com.codewithcled.fullstack_backend_proj1.DTO.TournamentDTO;
import com.codewithcled.fullstack_backend_proj1.DTO.UserDTO;
import com.codewithcled.fullstack_backend_proj1.DTO.UserMapper;
import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.repository.TournamentRepository;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;
import com.codewithcled.fullstack_backend_proj1.service.TournamentServiceImplementation;
import com.codewithcled.fullstack_backend_proj1.service.UserServiceImplementation;

import reactor.core.publisher.Flux;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "36000")
public class SSEControllerIntegrationTest {

    @LocalServerPort
    private int port;

    private final String baseUrl = "http://localhost:";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private WebTestClient webClient;

    @Autowired
    PasswordEncoder passwordEncoder;

    private String urlPrefix="/update";

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
        tournamentRepository.deleteAll();
    }

    @Test
    public void testGetUserDTOS() throws Exception {
        URI uri = new URI(baseUrl + port + urlPrefix+"/sse/users");
        User testUser = new User();
        testUser.setUsername("testUser");
        testUser.setRole("ROLE_USER");
        testUser.setPassword(passwordEncoder.encode("TestPassword"));
        testUser.setId((long) 110);
        testUser.setEmail("testUser");
        testUser.setElo(1000.0);
        testUser.setCurrentTournaments(new ArrayList<Tournament>());
        userRepository.save(testUser);

        webClient.get().uri(uri).exchange().expectStatus().isOk();

    }

    @Test
    public void testGetUsersStream() throws Exception {
        URI uri = new URI(baseUrl + port +urlPrefix +"/sse/user");
        User testUser = new User();
        testUser.setUsername("testUser");
        testUser.setRole("ROLE_USER");
        testUser.setPassword(passwordEncoder.encode("TestPassword"));
        testUser.setId((long) 110);
        testUser.setEmail("testUser");
        testUser.setElo(1000.0);
        testUser.setCurrentTournaments(new ArrayList<Tournament>());
        userRepository.save(testUser);

        webClient.get().uri(uri).exchange().expectStatus().isOk();

    }

    @Test
    public void testGetTournament() throws Exception {
        URI uri = new URI(baseUrl + port + urlPrefix+"/sse/tournament");
        Tournament testTournament = new Tournament();
        testTournament.setTournament_name("testTournament");
        testTournament.setDate("22/12/2013");
        testTournament.setActive("Active");
        testTournament.setSize(0);
        testTournament.setNoOfRounds(0);
        tournamentRepository.save(testTournament);

        webClient.get().uri(uri).exchange().expectStatus().isOk();

    }

}
