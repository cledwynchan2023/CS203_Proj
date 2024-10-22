package com.codewithcled.fullstack_backend_proj1.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.codewithcled.fullstack_backend_proj1.DTO.MatchDTO;
import com.codewithcled.fullstack_backend_proj1.DTO.MatchDTOMapper;
import com.codewithcled.fullstack_backend_proj1.DTO.UserMapper;
import com.codewithcled.fullstack_backend_proj1.model.Match;
import com.codewithcled.fullstack_backend_proj1.repository.MatchRepository;
import com.codewithcled.fullstack_backend_proj1.service.RoundService;

@RestController
@RequestMapping("/r")
public class RoundController {

    @Autowired
    RoundService roundService;

    @GetMapping({"/round/{id}/roundService/checkComplete"})
    public ResponseEntity<String> checkRoundComplete(@PathVariable("id") Long id) throws Exception{
        roundService.checkComplete(id);
        return ResponseEntity.ok("Successfully checked roundService.isComplete");
    }

    @GetMapping("/round/{id}/matches")
    public ResponseEntity<List<MatchDTO>> getAllMatches(@PathVariable("id") Long id) throws Exception {
        List<Match> matches = roundService.getAllMatches(id);
        if (matches.isEmpty()) {
            return ResponseEntity.noContent().build();  // Return 204 No Content if the list is empty
        }
        List<MatchDTO> matchDTOs = MatchDTOMapper.toDTOList(matches);
        return ResponseEntity.ok(matchDTOs);  // Return 200 OK with the list of MatchDTOs
    }

}
