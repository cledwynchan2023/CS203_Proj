package com.codewithcled.fullstack_backend_proj1.service;

import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.repository.TournamentRepository;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codewithcled.fullstack_backend_proj1.DTO.CreateTournamentRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.TournamentDTO;
import com.codewithcled.fullstack_backend_proj1.DTO.TournamentMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


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
                
        if (user.getCurrentTournaments().contains(currentTournament)) {
            System.out.println(currentTournament.getParticipants());
            user.removeCurrentTournament(currentTournament);
            System.out.println(currentTournament.getParticipants().size());
            currentTournament.setCurrentSize(currentTournament.getParticipants().size() - 1);
            userRepository.save(user);
        } else {

            throw new Exception("User is not participating in the tournament");
        }
        return tournamentRepository.save(currentTournament);  // Save and return the updated tournament
    }
    @Override
    public Tournament updateTournament(Long id, CreateTournamentRequest newTournament) throws Exception {
        return tournamentRepository.findById(id)
                .map(tournament -> {
                    if (newTournament.getTournament_name() != null) {
                        tournament.setTournament_name(newTournament.getTournament_name());
                    }
                    if (newTournament.getDate() != null) {
                        tournament.setDate(newTournament.getDate());
                    }
                    if (newTournament.getSize() != null) {
                        tournament.setSize(newTournament.getSize());
                    }
                    if (newTournament.getStatus() != null) {
                        tournament.setActive(newTournament.getStatus());
                    }
                    if (newTournament.getNoOfRounds() != null) {
                        tournament.setNoOfRounds(newTournament.getNoOfRounds());
                    }
                    if (newTournament.getCurrentSize() != null) {
                        tournament.setCurrentSize(newTournament.getCurrentSize());
                    }
                    return tournamentRepository.save(tournament);  // Save and return the updated tournament
                })
                .orElseThrow(() -> new Exception("Tournament not found"));  // Throw exception if tournament does not exist
    }
    @Override
    public List<Tournament> getTournamentsWithNoCurrentUser(Long userId) throws Exception {
      
            List<Tournament> list = getAllTournament();
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new Exception("User not found"));

            for (int i = 0; i < list.size(); i++) {
                Tournament tournament = list.get(i);
                 if (tournament.getParticipants().contains(user)) {
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

    @Override
    public List<User> getNonParticipatingCurrentUser(Long tournamentId) throws Exception {
        // TODO Auto-generated method stub
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new Exception("Tournament not found"));
        List<User> userList = userRepository.findAll();
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            List<Tournament> currentTournaments = user.getCurrentTournaments();
            if (user.getRole().equals("ROLE_ADMIN")) {
                userList.remove(user);
            } else {
                for (Tournament tour: currentTournaments) {
                    if (tour.getId() == tournamentId) {
                        userList.remove(user);
                    }
                }
            }

       }
       return Optional.ofNullable(userList).orElseGet(ArrayList::new);
    }

    @Override
    @Transactional(readOnly=true)
    public List<TournamentDTO> findAllTournamentsDTO() throws Exception {
        List<Tournament> tournaments = tournamentRepository.findAll();
        return TournamentMapper.toDTOList(tournaments);
    }



}
