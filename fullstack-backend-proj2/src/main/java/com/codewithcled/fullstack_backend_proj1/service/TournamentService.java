package com.codewithcled.fullstack_backend_proj1.service;

import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.DTO.CreateTournamentRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.TournamentDTO;
import java.util.List;

/**
 * Tournament Service Interface
 */
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

    /**
     * Get all completed tournaments
     * @return List of all completed tournaments
     */
    public List<Tournament> getCompletedTournament();

    /**
     * Get all ongoing tournaments
     * @return List of all ongoing tournaments
     */
    public List<Tournament> getOngoingTournament();

    /**
     * Get all participants of a tournament
     * @param id Tournament id
     * @return List of all participants of a tournament
     * @throws Exception
     */
    public List<User> getTournamentParticipants (Long id) throws Exception;

    /**
     * Update tournament participants by adding a user
     * @param userId User id of the user to be added
     * @param tournamentId tournament id of the tournament to be updated
     * @return Updated tournament
     * @throws Exception
     */
    public Tournament updateUserParticipating (Long userId, Long tournamentId) throws Exception;

    /**
     * Update tournament participants by removing a user
     * @param userId User id of the user to be removed
     * @param tournamentId tournament id of the tournament to be updated
     * @return Updated tournament
     * @throws Exception
     */
    public Tournament removeUserParticipating(Long userId, Long tournamentId) throws Exception;

    /**
     * Update tournament details
     * @param id Tournament id
     * @param newTournament New tournament details
     * @return Updated tournament
     * @throws Exception
     */
    public Tournament updateTournament(Long id, CreateTournamentRequest newTournament) throws Exception;

    /**
     * Get all tournaments that the current user is not participating in
     * @param userId User id of the current user
     * @return List of all tournaments that the current user is not participating in
     * @throws Exception
     */
    public List<Tournament> getTournamentsCurrentUserNotIn (Long userId) throws Exception;

    /**
     * Create a new tournament with the given details
     * @param tournament Tournament details
     * @return Created tournament
     * @throws Exception
     */
    public Tournament createTournament(CreateTournamentRequest tournament) throws Exception;

    /**
     * Get all users not participating in the current tournament
     * @param id Tournament id
     * @return List of all users not participating in the current tournament
     * @throws Exception
     */
    public List<User> getUsersNotInCurrentTournament(Long id) throws Exception;
    
    /**
     * Get all tournaments sorted by name
     * @return List of all tournaments sorted by name
     * @throws Exception
     */
    public List<Tournament> getTournamentsSortedByName() throws Exception;

    /**
     * Get all tournaments sorted by date
     * @return List of all tournaments sorted by date
     * @throws Exception
     */
    public List<Tournament> getTournamentsSortedByDate() throws Exception;

    /**
     * Get all tournaments sorted by size
     * @return List of all tournaments sorted by size
     * @throws Exception
     */
    public List<Tournament> getTournamentsSortedBySize() throws Exception;

    /**
     * Get a list of all tournaments in DTO format
     * @return List of all tournaments converted to DTO format
     * @throws Exception
     */
    public List<TournamentDTO> findAllTournamentsDTO() throws Exception;

    /**
     * Start a tournament
     * @param id Tournament id
     * @return Started tournament
     * @throws Exception
     */
    public Tournament startTournament(Long id) throws Exception;

    /**
     * Check if a tournament is complete
     * @param id Tournament id
     * @throws Exception
     */
    public void checkComplete(Long id) throws Exception;

    /**
     * Delete a tournament
     * @param id Tournament id
     * @throws Exception
     */
    public void deleteTournament(Long id) throws Exception;

    /**
     * End a tournament
     * @param id Tournament id
     * @throws Exception
     */
    public void endTournament(Long id) throws Exception;

    /**
     * Remove all users from a tournament
     * @param id Tournament id
     * @throws Exception
     */
    public void removeAllUsers(Long id) throws Exception;
}
