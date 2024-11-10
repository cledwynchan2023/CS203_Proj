package com.codewithcled.fullstack_backend_proj1.service;

import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.DTO.CreateTournamentRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.TournamentDTO;
import java.util.List;

public interface TournamentService {

    /**
     * Get all tournaments
     * @return List of all tournaments
     */
    public List<Tournament> getAllTournament();

    /**
     * Get all active tournaments
     * @return List of all active tournaments
     */
    public List<Tournament> getActiveTournament();


    public List<Tournament> getCompletedTournament();
    public List<Tournament> getOngoingTournament();
    public Tournament findTournamentByName(String name);

    public List<User> getTournamentParticipants (Long id) throws Exception;

    public Tournament updateUserParticipating (Long userId, Long id) throws Exception;

    public Tournament removeUserParticipating(Long userId, Long id) throws Exception;

    public Tournament updateTournament(Long id, CreateTournamentRequest newTournament) throws Exception;

    public List<Tournament> getTournamentsWithNoCurrentUser (Long userId) throws Exception;

    public Tournament createTournament(CreateTournamentRequest tournament) throws Exception;

    public List<User> getNonParticipatingCurrentUser(Long tournamentId) throws Exception;
    
    public List<Tournament> getFilteredTournamentsByName() throws Exception;

    public List<Tournament> getFilteredTournamentsByDate() throws Exception;

    public List<Tournament> getFilteredTournamentsBySize() throws Exception;

    public List<TournamentDTO> findAllTournamentsDTO() throws Exception;

    public Tournament startTournament(Long id) throws Exception;

    public void checkComplete(Long tournamentId) throws Exception;

    public void deleteTournament(Long id) throws Exception;

    public void endTournament(Long id) throws Exception;

    public void removeAllUsers(Long id) throws Exception;
}
