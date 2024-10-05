package com.codewithcled.fullstack_backend_proj1.service;

import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.repository.TournamentRepository;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codewithcled.fullstack_backend_proj1.DTO.CreateTournamentRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.TournamentDTO;
import com.codewithcled.fullstack_backend_proj1.DTO.UserDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TournamentServiceImplementation implements TournamentService{


    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private UserRepository userRepository;

    public TournamentServiceImplementation(TournamentRepository tournamentRepository) {
        this.tournamentRepository=tournamentRepository;
    }
    @Override
    public List<Tournament> getAllTournament() {
        return tournamentRepository.findAll();
    }

    @Override
    public Tournament findTournamentByName(String name) {
        return null;
    }

    @Override
    public List<User> getTournamentParticipants(Long id) throws Exception {
        Tournament currentTournament =  tournamentRepository.findById(id)
                .orElseThrow(() -> new Exception("Error Occured"));

        return currentTournament.getParticipants();
    }

    @Override
    public Tournament updateUserParticipating(Long userId, Long id) throws Exception {
        Tournament currentTournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new Exception("Tournament not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        if (!user.getCurrentTournaments().contains(currentTournament)) {
                    user.addCurrentTournament(currentTournament);
        }
        if (!currentTournament.getParticipants().contains(user)) {
            currentTournament.addParticipant(user);
        }
        userRepository.save(user);
        currentTournament.setCurrentSize(currentTournament.getParticipants().size());
        return tournamentRepository.save(currentTournament);
    }

    @Override
    public Tournament removeUserParticipating(Long userId, Long id) throws Exception {
        Tournament currentTournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new Exception("Tournament not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));
        System.out.println("deleting user");
        // Check if the user is in the participant list before attempting to remove
        if (currentTournament.getParticipants().contains(userId)) {
            currentTournament.removeParticipant(user);  // Remove the user
            currentTournament.setCurrentSize(currentTournament.getCurrentSize() - 1);
        } else {
            throw new Exception("User is not participating in the tournament");
        }

        return tournamentRepository.save(currentTournament);  // Save and return the updated tournament
    }
    @Override
    public Tournament updateTournament(Long id, Tournament newTournament) throws Exception {
        return tournamentRepository.findById(id)
                .map(tournament -> {
                    tournament.setTournament_name(newTournament.getTournament_name());
                    tournament.setDate(newTournament.getDate());
                    tournament.setSize(newTournament.getSize());
                    tournament.setActive(newTournament.getStatus());
                    tournament.setNoOfRounds(newTournament.getNoOfRounds());
                    tournament.setParticipants(newTournament.getParticipants());
                    return tournamentRepository.save(tournament);  // Save and return the updated tournament
                })
                .orElseThrow(() -> new Exception("Tournament not found"));  // Throw exception if tournament does not exist
    }
    @Override
    public List<Tournament> getTournamentsWithNoCurrentUser(Long userId) throws Exception {
      
            List<Tournament> list = getAllTournament();
        //     System.out.println("list" + list);
        //     
            for (int i = 0; i < list.size(); i++) {
                Tournament tournament = list.get(i);
                 if (tournament.getParticipants().contains(userId)) {
                   list.remove(tournament);
                 }
           }
           
           
           
           return Optional.ofNullable(list).orElseGet(ArrayList::new);
    
    }
    @Override
    public Tournament createTournament(CreateTournamentRequest tournament) throws Exception {
        String tournament_name= tournament.getTournament_name();
        String date = tournament.getDate();
        String status = tournament.getStatus();
        Integer size = tournament.getSize();
        Integer noOfRounds = tournament.getNoOfRounds();


        // Tournament isEmailExist = tournamentRepository.findBy(email);
        // if (isEmailExist != null) {
        //     System.out.println("Email Taken!");
        //     throw new Exception("Email Is Already Used With Another Account");
        // }

        // if (userRepository.existsByUsername(username)){
        //     System.out.println("Username Taken!");
        //     throw new Exception("Username is already being used with another account");
        // }
        Tournament createdTournament = new Tournament();
        createdTournament.setTournament_name(tournament_name);
        createdTournament.setDate(date);
        createdTournament.setActive(status);
        createdTournament.setSize(size);
        createdTournament.setNoOfRounds(noOfRounds);
        
        return tournamentRepository.save(createdTournament);
    }

    // @Override
    // public List<TournamentDTO> findAllTournamentsDTO() throws Exception {
    //     return tournamentRepository.findAll().stream()
    //     .map(tournament -> new TournamentDTO(tournament.getId(),tournament.getTournament_name(),tournament.getParticipants(), tournament.getScoreboard(),tournament.getDate(),  tournament.getStatus(),tournament.getSize(), tournament.getCurrentSize(), tournament.getNoOfRounds(), tournament.getRounds()))
    //     .collect(Collectors.toList());
    // }



}
