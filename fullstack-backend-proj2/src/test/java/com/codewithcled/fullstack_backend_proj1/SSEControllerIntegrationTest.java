package com.codewithcled.fullstack_backend_proj1;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.repository.TournamentRepository;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;

import java.net.URI;
import java.util.ArrayList;

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
    public void testGetUserDTOs() throws Exception {
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
        testTournament.setStatus("active");
        testTournament.setSize(0);
        testTournament.setNoOfRounds(0);
        tournamentRepository.save(testTournament);

        webClient.get().uri(uri).exchange().expectStatus().isOk();

    }

}
