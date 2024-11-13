package com.codewithcled.fullstack_backend_proj1.controller;

import com.codewithcled.fullstack_backend_proj1.DTO.CreateTournamentRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.RoundDTO;
import com.codewithcled.fullstack_backend_proj1.DTO.RoundMapper;
import com.codewithcled.fullstack_backend_proj1.DTO.TournamentDTO;
import com.codewithcled.fullstack_backend_proj1.DTO.TournamentMapper;
import com.codewithcled.fullstack_backend_proj1.DTO.TournamentStartDTO;
import com.codewithcled.fullstack_backend_proj1.DTO.TournamentStartMapper;
import com.codewithcled.fullstack_backend_proj1.DTO.UserDTO;
import com.codewithcled.fullstack_backend_proj1.DTO.UserMapper;
import com.codewithcled.fullstack_backend_proj1.model.Round;
import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.repository.TournamentRepository;
import com.codewithcled.fullstack_backend_proj1.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.NoSuchElementException;

/*
 * Tournament Controller
 * Authorisation includes:
 * - Get all Tournaments
 * - Get Tournament by ID
 * - Get tournament participants
 */
@RestController
@RequestMapping("/t")
public class TournamentController {
    @Autowired
    private TournamentRepository tournamentRepository;
    @Autowired
    private TournamentService tournamentService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/tournaments")
    public ResponseEntity<List<TournamentDTO>> getAllTournaments() {
        List<Tournament> tournaments = tournamentRepository.findAll();
        if (tournaments.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 No Content if the list is empty
        }
        List<TournamentDTO> tournamentDTOs = TournamentMapper.toDTOList(tournaments);
        return ResponseEntity.ok(tournamentDTOs); // Return 200 OK with the list of TournamentDTOs
    }

    // get active tournaments
    @GetMapping("/tournaments/active")
    public ResponseEntity<List<TournamentDTO>> getActiveTournaments() {
        List<Tournament> tournaments = tournamentService.getActiveTournament();
        if (tournaments.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 No Content if the list is empty
        }
        List<TournamentDTO> tournamentDTOs = TournamentMapper.toDTOList(tournaments);
        return ResponseEntity.ok(tournamentDTOs); // Return 200 OK with the list of TournamentDTOs
    }

    // get completed tournaments
    @GetMapping("/tournaments/completed")
    public ResponseEntity<List<TournamentDTO>> getCompletedTournaments() {
        List<Tournament> tournaments = tournamentService.getCompletedTournament();
        if (tournaments.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 No Content if the list is empty
        }
        List<TournamentDTO> tournamentDTOs = TournamentMapper.toDTOList(tournaments);
        return ResponseEntity.ok(tournamentDTOs); // Return 200 OK with the list of TournamentDTOs
    }

    @GetMapping("/tournaments/ongoing")
    public ResponseEntity<List<TournamentDTO>> getOngoingTournaments() {
        List<Tournament> tournaments = tournamentService.getOngoingTournament();
        if (tournaments.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 No Content if the list is empty
        }
        List<TournamentDTO> tournamentDTOs = TournamentMapper.toDTOList(tournaments);
        return ResponseEntity.ok(tournamentDTOs); // Return 200 OK with the list of TournamentDTOs
    }

    @GetMapping("/tournaments/name")
    public ResponseEntity<List<TournamentDTO>> getFilteredTournamentsByName() throws Exception {
        List<Tournament> tournaments = tournamentService.getTournamentsSortedByName();
        if (tournaments.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 No Content if the list is empty
        }
        List<TournamentDTO> tournamentDTOs = TournamentMapper.toDTOList(tournaments);
        return ResponseEntity.ok(tournamentDTOs); // Return 200 OK with the list of TournamentDTOs
    }

    @GetMapping("/tournaments/date")
    public ResponseEntity<List<TournamentDTO>> getFilteredTournamentsByDate() throws Exception {
        List<Tournament> tournaments = tournamentService.getTournamentsSortedByDate();
        if (tournaments.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 No Content if the list is empty
        }
        List<TournamentDTO> tournamentDTOs = TournamentMapper.toDTOList(tournaments);
        return ResponseEntity.ok(tournamentDTOs); // Return 200 OK with the list of TournamentDTOs
    }

    @GetMapping("/tournaments/capacity")
    public ResponseEntity<List<TournamentDTO>> getFilteredTournamentsBySize() throws Exception {
        List<Tournament> tournaments = tournamentService.getTournamentsSortedBySize();
        if (tournaments.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 No Content if the list is empty
        }
        List<TournamentDTO> tournamentDTOs = TournamentMapper.toDTOList(tournaments);
        return ResponseEntity.ok(tournamentDTOs); // Return 200 OK with the list of TournamentDTOs
    }

    @GetMapping("/{id}")
    public ResponseEntity<TournamentDTO> getTournamentById(@PathVariable("id") Long id) {
        return tournamentRepository.findById(id)
                .map(tournament -> ResponseEntity.ok(TournamentMapper.toDTO(tournament)))
                .orElse(ResponseEntity.notFound().build()); // Return 404 Not Found if the tournament is not found
    }

    @PostMapping("/tournament/{id}/round")
    public ResponseEntity<String> addRound(@RequestBody Round round, @PathVariable("id") Long id) throws Exception {
        // Find the tournament by ID
        Tournament currentTournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));

        // Set the tournament for the round (to maintain the bidirectional relationship)
        round.setTournament(currentTournament);

        // Add the round to the tournament's list of rounds
        currentTournament.getRounds().add(round);

        // Save the updated tournament (with rounds cascading should save the round)
        tournamentRepository.save(currentTournament);

        return ResponseEntity.ok("Round added successfully to the tournament");
    }

    @GetMapping("/{id}/participant")
    public ResponseEntity<List<UserDTO>> getTournamentParticipants(@PathVariable("id") Long id) {
        try {
            List<User> participants = tournamentService.getTournamentParticipants(id);
            List<UserDTO> participantDTOs = UserMapper.toDTOList(participants);
            return ResponseEntity.ok(participantDTOs); // Return 200 OK with the list of UserDTOs
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Return 404 if tournament not found
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}/participant/add")
    public ResponseEntity<TournamentDTO> updateTournamentParticipant(@RequestParam("user_id") Long userId,
            @PathVariable("id") Long id) throws Exception {
        try {
            Tournament updatedTournament = tournamentService.updateUserParticipating(userId, id);
            TournamentDTO tournamentDTO = TournamentMapper.toDTO(updatedTournament);
            messagingTemplate.convertAndSend("/topic/tournamentCreate", "Tournament edited");
            return new ResponseEntity<>(tournamentDTO, HttpStatus.OK); // Return 200 OK with the updated tournament
        } catch (Exception e) {
            System.out.println("error " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Return 400 Bad Request for errors
        }
    }

    @GetMapping("/tournaments/{id}")
    public ResponseEntity<List<TournamentDTO>> getTournamentWithNoCurrentUser(@PathVariable("id") Long id)
            throws Exception {
        try {
            List<Tournament> tournaments = tournamentService.getTournamentsCurrentUserNotIn(id);
            List<TournamentDTO> tournamentDTOs = TournamentMapper.toDTOList(tournaments);
            return new ResponseEntity<>(tournamentDTOs, HttpStatus.OK); // Return 200 OK with the list of TournamentDTOs
        } catch (Exception e) {
            System.out.println("error " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Return 400 Bad Request for errors
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<List<UserDTO>> getUsersWithNoCurrentTournament(@PathVariable("id") Long id) throws Exception {
        try {
            List<User> users = tournamentService.getUsersNotInCurrentTournament(id);
            List<UserDTO> userDTOs = UserMapper.toDTOList(users);
            return new ResponseEntity<>(userDTOs, HttpStatus.OK); // Return 200 OK with the list of TournamentDTOs
        } catch (Exception e) {
            System.out.println("error " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Return 400 Bad Request for errors
        }
    }

    @PutMapping("/{id}/participant/delete")
    public ResponseEntity<TournamentDTO> removeParticipant(
            @RequestParam("user_id") Long userId,
            @PathVariable("id") Long id) {
        try {
            System.out.println("removing");
            Tournament updatedTournament = tournamentService.removeUserParticipating(userId, id);
            TournamentDTO tournamentDTO = TournamentMapper.toDTO(updatedTournament);
            return new ResponseEntity<>(tournamentDTO, HttpStatus.OK); // Return 200 OK with the updated tournament
        } catch (Exception e) {
            // Log the exception message for debugging
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Return 400 Bad Request for errors
        }
    }

    @DeleteMapping("/tournament/{id}")
    public ResponseEntity<String> deleteTournament(@PathVariable("id") Long id) {
        try {

            System.out.println("Deleting tournament with ID " + id);
            tournamentService.deleteTournament(id);
            messagingTemplate.convertAndSend("/topic/tournamentCreate", "Tournament deleted");
            return ResponseEntity.ok("Tournament with ID " + id + " has been deleted."); // Return 200 OK with success
                                                                                         // message
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while deleting the tournament."); // Return 500 Internal Server Error for
                                                                               // other issues
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TournamentDTO> updateTournament(@RequestBody CreateTournamentRequest newTournament,
            @PathVariable("id") Long id) {
        try {
            System.out.println(newTournament.getTournament_name());
            Tournament updatedTournament = tournamentService.updateTournament(id, newTournament);
            TournamentDTO tournamentDTO = TournamentMapper.toDTO(updatedTournament);
            // sseController.sendTournamentUpdate(updatedTournament);
            messagingTemplate.convertAndSend("/topic/tournamentCreate", "Tournament edited");

            return ResponseEntity.ok(tournamentDTO); // Return 200 OK with the updated TournamentDTO
        } catch (Exception e) {
            // Log the exception message for debugging
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Return 400 Bad Request for errors
        }
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<TournamentDTO> startTournament(@PathVariable("id") Long id) {
        try {
            Tournament updatedTournament = tournamentService.startTournament(id);
            TournamentDTO tournamentDTO = TournamentMapper.toDTO(updatedTournament);
            messagingTemplate.convertAndSend("/topic/tournamentCreate", "Tournament Start");
            System.out.println("Tournament started");
            return ResponseEntity.ok(tournamentDTO); // Return 200 OK with the updated TournamentDTO
        } catch (Exception e) {
            // Log the exception message for debugging
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Return 400 Bad Request for errors
        }
    }

    @GetMapping("/tournament/{id}/start")
    public ResponseEntity<TournamentStartDTO> startTournamentService(@PathVariable("id") Long id) throws Exception {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        TournamentStartDTO tournamentStartDTO = TournamentStartMapper.toDTO(tournament);
        return ResponseEntity.ok(tournamentStartDTO);
    }

    @GetMapping("/tournament/{id}/start/rounds")
    public ResponseEntity<List<RoundDTO>> getAllRounds(@PathVariable("id") Long id) throws Exception {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        List<Round> rounds = tournament.getRounds();
        List<RoundDTO> newRounds = RoundMapper.toDTOList(rounds);
        return ResponseEntity.ok(newRounds);
    }

    @GetMapping({ "/tournament/{id}/checkComplete" })
    public ResponseEntity<String> checkTournamentComplete(@PathVariable("id") Long id) throws Exception {
        tournamentService.checkComplete(id);
        return ResponseEntity.ok("Successfully checked tournamentService.isComplete");
    }
}
