package com.codewithcled.fullstack_backend_proj1.service;

import com.codewithcled.fullstack_backend_proj1.DTO.UserDTO;
import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.response.AuthResponse;
import com.codewithcled.fullstack_backend_proj1.DTO.CreateTournamentRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.TournamentDTO;
import java.util.List;
import java.util.Optional;

public interface TournamentService {

    public List<Tournament> getAllTournament()  ;


    public Tournament findTournamentByName(String name) ;

    public List<User> getTournamentParticipants (Long id) throws Exception;

    public Tournament updateUserParticipating (Long userId, Long id) throws Exception;

    public Tournament removeUserParticipating(Long userId, Long id) throws Exception;

    public Tournament updateTournament(Long id, Tournament newTournament) throws Exception;

    public List<Tournament> getTournamentsWithNoCurrentUser (Long userId) throws Exception;

    public Tournament createTournament(CreateTournamentRequest tournament) throws Exception;

    // public List<TournamentDTO> findAllTournamentsDTO() throws Exception;

}
