package com.codewithcled.fullstack_backend_proj1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.codewithcled.fullstack_backend_proj1.DTO.TournamentDTO;
import com.codewithcled.fullstack_backend_proj1.DTO.UserDTO;
import com.codewithcled.fullstack_backend_proj1.service.UserServiceImplementation;
import com.codewithcled.fullstack_backend_proj1.service.TournamentService;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;
import java.time.Duration;

@RestController
@RequestMapping("/update")
public class SSEController {

    @Autowired
    UserServiceImplementation userService;

    @Autowired
    TournamentService tournamentService;

    private final int timer = 2;

    @CrossOrigin(allowedHeaders = "*")
    @GetMapping(value = "/sse/users", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<List<UserDTO>> getMethodName() {
        return Flux.interval(Duration.ofSeconds(timer)).map(tick -> userService.findAllUsersDTO());
    }

    @GetMapping(value = "/sse/user", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public List<UserDTO> getUserDTOS() {
        return userService.findAllUsersDTO();
    }

    @GetMapping(value = "/sse/tournament", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<List<TournamentDTO>> getTournament() {
        return Flux.interval(Duration.ofSeconds(timer))
                .map(tick -> {
                    try {
                        return tournamentService.findAllTournamentsDTO();
                    } catch (Exception e) {
                        // Log the exception and return an empty list
                        System.out.println("Error occurred: " + e.getMessage());

                        return Collections.emptyList();
                    }
                });
    }

}