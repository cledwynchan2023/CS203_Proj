package com.codewithcled.fullstack_backend_proj1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.codewithcled.fullstack_backend_proj1.DTO.UserMapper;
import com.codewithcled.fullstack_backend_proj1.repository.MatchRepository;
import com.codewithcled.fullstack_backend_proj1.service.RoundService;

public class RoundController {

    @Autowired
    RoundService roundService;

    @GetMapping({"/round/{id}/roundService/checkComplete"})
    public ResponseEntity<String> checkRoundComplete(@PathVariable("id") Long id) throws Exception{
        roundService.checkComplete(id);
        return ResponseEntity.ok("Successfully checked roundService.isComplete");
    }
}
