package com.codewithcled.fullstack_backend_proj1.controller;

import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codewithcled.fullstack_backend_proj1.DTO.ResultRequest;
import com.codewithcled.fullstack_backend_proj1.repository.MatchRepository;
import com.codewithcled.fullstack_backend_proj1.service.MatchService;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/m")
public class MatchController {

    @Autowired
    MatchRepository matchRepository;

    @Autowired
    MatchService matchService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PutMapping("/match/{id}/update")
    public ResponseEntity<String> updateMatch(@RequestBody ResultRequest result, @PathVariable("id") Long id) throws Exception{
        Integer resultId = result.getResult();
        matchService.updateMatch(id, resultId);
        System.out.println(resultId);
        messagingTemplate.convertAndSend("/topic/matchUpdates", "Match " + id + " updated");
        return ResponseEntity.ok("Match result updated successfully");
    }

    @GetMapping("/match/{id}/getPlayers")
    public ResponseEntity<String[]> getPlayers(@PathVariable("id") Long id) throws Exception{
        String[] players = matchService.getPlayers(id);
        return ResponseEntity.ok(players);
    }
    
}
