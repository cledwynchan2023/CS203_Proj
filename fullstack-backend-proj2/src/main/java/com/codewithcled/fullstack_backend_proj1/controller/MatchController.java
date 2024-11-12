package com.codewithcled.fullstack_backend_proj1.controller;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import com.codewithcled.fullstack_backend_proj1.DTO.ResultRequest;
import com.codewithcled.fullstack_backend_proj1.repository.MatchRepository;
import com.codewithcled.fullstack_backend_proj1.service.MatchService;

/**
 * Controller for handling match-related requests.
 */
@RestController
@RequestMapping("/m")
public class MatchController {

    /**
     * Repository for accessing match data.
     */
    @Autowired
    MatchRepository matchRepository;

    /**
     * Service for match-related business logic.
     */
    @Autowired
    MatchService matchService;

    /**
     * Template for sending messages to clients.
     */
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Updates the result of a match.
     *
     * @param result the result request containing the new result
     * @param id the ID of the match to update
     * @return a response entity with a success message
     * @throws Exception if an error occurs during the update
     */
    @PutMapping("/match/{id}/update")
    public ResponseEntity<String> updateMatch(@RequestBody ResultRequest result, @PathVariable("id") Long id) throws Exception {
        Integer resultId = result.getResult();
        matchService.updateMatch(id, resultId);
        System.out.println(resultId);
        messagingTemplate.convertAndSend("/topic/matchUpdates", "Match " + id + " updated");
        return ResponseEntity.ok("Match result updated successfully");
    }

    /**
     * Retrieves the usernames of the players in a match.
     *
     * @param id the ID of the match
     * @return a response entity with an array of player usernames
     * @throws Exception if an error occurs during retrieval
     */
    @GetMapping("/match/{id}/getPlayers")
    public ResponseEntity<String[]> getPlayerUsernames(@PathVariable("id") Long id) throws Exception {
        String[] players = matchService.getPlayerUsernames(id);
        return ResponseEntity.ok(players);
    }
}