package com.codewithcled.fullstack_backend_proj1.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.codewithcled.fullstack_backend_proj1.DTO.MatchDTO;
import com.codewithcled.fullstack_backend_proj1.DTO.MatchDTOMapper;
import com.codewithcled.fullstack_backend_proj1.model.Match;
import com.codewithcled.fullstack_backend_proj1.service.RoundService;

/**
 * REST controller for managing rounds in a tournament.
 */
@RestController
@RequestMapping("/r")
public class RoundController {

    @Autowired
    RoundService roundService;

    /**
     * Checks if a round is complete.
     *
     * @param id the ID of the round to check
     * @return a ResponseEntity with a message indicating the result of the check
     * @throws Exception if an error occurs during the check
     */
    @GetMapping({"/round/{id}/checkComplete"})
    public ResponseEntity<String> checkRoundComplete(@PathVariable("id") Long id) throws Exception{
        roundService.checkComplete(id);
        return ResponseEntity.ok("Successfully checked roundService.isComplete");
    }

    /**
     * Retrieves all matches for a specified round.
     *
     * @param id the ID of the round
     * @return a ResponseEntity with a list of MatchDTOs or a 204 No Content status if the list is empty
     * @throws Exception if an error occurs during retrieval
     */
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
