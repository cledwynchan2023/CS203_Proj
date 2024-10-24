package com.codewithcled.fullstack_backend_proj1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.codewithcled.fullstack_backend_proj1.repository.MatchRepository;
import com.codewithcled.fullstack_backend_proj1.service.MatchService;

@RestController
@RequestMapping("/m")
public class MatchController {

    @Autowired
    MatchRepository matchRepository;

    @Autowired
    MatchService matchService;

    @PutMapping({"/match/{id}/update"})
    public ResponseEntity<String> updateMatch(@RequestBody int result, @PathVariable("id") Long id) throws Exception{
        matchService.updateMatch(id, result);
        return ResponseEntity.ok("Match result updated successfully");
    }
}
