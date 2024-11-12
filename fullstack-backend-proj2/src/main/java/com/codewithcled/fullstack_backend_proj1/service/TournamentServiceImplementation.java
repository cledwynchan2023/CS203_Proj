package com.codewithcled.fullstack_backend_proj1.service;

import com.codewithcled.fullstack_backend_proj1.model.Round;
import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.repository.TournamentRepository;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;
import com.codewithcled.fullstack_backend_proj1.DTO.CreateTournamentRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.TournamentDTO;
import com.codewithcled.fullstack_backend_proj1.DTO.TournamentMapper;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Implementation of the TournamentService interface
 */
@Service
public class TournamentServiceImplementation implements TournamentService {

    // for debugging purposes
    private static final Logger logger = Logger.getLogger(TournamentServiceImplementation.class.getName());

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoundService roundService;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tournament> getAllTournament() {
        return tournamentRepository.findAll();
    }

    /**
     * Helper method to filter tournaments by status
     * @param tournaments list of tournaments
     * @param status will be either "active", "ongoing" or "completed"
     * @return list of tournaments with the given status
     */
    private List<Tournament> filterTournamentsByStatus(List<Tournament> tournaments, String status) {
        List<Tournament> filteredTournaments = new ArrayList<>();
        for (Tournament tournament : tournaments) {
            if (tournament.getStatus().equals(status)) {
                filteredTournaments.add(tournament);
            }
        }
        return filteredTournaments;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tournament> getActiveTournament() {
        return filterTournamentsByStatus(getAllTournament(), "active");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tournament> getOngoingTournament() {
        return filterTournamentsByStatus(getAllTournament(), "ongoing");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tournament> getCompletedTournament() {
        return filterTournamentsByStatus(getAllTournament(), "completed");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> getTournamentParticipants(Long id) throws Exception {
        Tournament currentTournament = findTournamentById(id);
        return currentTournament.getParticipants();
    }

    /**
     * Helper method to find a tournament by its id
     * @param id the id of the tournament
     * @return the tournament with the given id
     * @throws NoSuchElementException if the tournament is not found
     */
    private Tournament findTournamentById(Long id) throws NoSuchElementException {
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tournament updateUserParticipating(Long userId, Long id) throws Exception {
        Tournament currentTournament = findTournamentById(id);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        // Check if the tournament is full
        if (currentTournament.getCurrentSize() >= currentTournament.getSize()) {
            throw new Exception("Tournament is full");
        }

        // Record tournament in user's own list and add user to tournament
        if (!user.getCurrentTournaments().contains(currentTournament) && currentTournament.getCurrentSize() < currentTournament.getSize()) {
            user.addCurrentTournament(currentTournament);
            userRepository.save(user);
            currentTournament.setCurrentSize(currentTournament.getParticipants().size());
        }
        
        return tournamentRepository.save(currentTournament);
    }

    /**
     * Helper method to add a tournament to a user's own list
     * Checks if the tournament is already in the user's list
     * @param currentTournament
     * @param user
     */
    private void addTournamentToUserOwnList(Tournament currentTournament, User user) {
        if (!user.getCurrentTournaments().contains(currentTournament) && currentTournament.getCurrentSize() < currentTournament.getSize()) {
            user.addCurrentTournament(currentTournament);
            userRepository.save(user);
            currentTournament.setCurrentSize(currentTournament.getParticipants().size());
        }
    }

    /**
     * Helper method to add a user to a tournament
     * Checks if the user is already in the tournament
     * @param currentTournament
     * @param user
     */
    private void addUserToTournament(Tournament currentTournament, User user) {
        if (!currentTournament.getParticipants().contains(user)) {
            currentTournament.addParticipant(user);
            currentTournament.setCurrentSize(currentTournament.getParticipants().size());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tournament removeUserParticipating(Long userId, Long id) throws Exception {
        Tournament currentTournament = findTournamentById(id);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        // Remove user from tournament
        removeUserFromTournament(currentTournament, user);

        return tournamentRepository.save(currentTournament);
    }

    /**
     * Helper method to remove a user from a tournament
     * Checks if the user is already in the tournament
     * @param currentTournament
     * @param user
     */
    private void removeUserFromTournament(Tournament currentTournament, User user) throws Exception {
        if (currentTournament.getParticipants().contains(user)) {
            currentTournament.removeParticipant(user);
            currentTournament.setCurrentSize(currentTournament.getCurrentSize() - 1);
            user.getCurrentTournaments().remove(currentTournament);
            userRepository.save(user);
        } else {
            throw new Exception("User is not participating in the tournament");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tournament updateTournament(Long id, CreateTournamentRequest newTournament) throws Exception {
        // Set the new tournament details if they are not null
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
                        tournament.setStatus(newTournament.getStatus());
                    }
                    if (newTournament.getNoOfRounds() != null) {
                        tournament.setNoOfRounds(newTournament.getNoOfRounds());
                    }
                    if (newTournament.getCurrentSize() != null) {
                        tournament.setCurrentSize(newTournament.getCurrentSize());
                    }
                    return tournamentRepository.save(tournament);
                })
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tournament> getTournamentsCurrentUserNotIn(Long userId) throws Exception {
        List<Tournament> list = getAllTournament();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        for (Tournament tournament : list) {
            if (tournament.getParticipants().contains(user)) {
                list.remove(tournament);
            }
        }

        return Optional.ofNullable(list).orElseGet(ArrayList::new);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tournament createTournament(CreateTournamentRequest tournament) throws Exception {
        // Check if tournament size is even
        if (tournament.getSize() % 2 != 0) {
            throw new Exception("Tournament size must be even");
        }

        // Place the tournament details in a new Tournament object
        Tournament createdTournament = new Tournament();
        createdTournament.setTournament_name(tournament.getTournament_name());
        createdTournament.setDate(tournament.getDate());
        createdTournament.setStatus(tournament.getStatus());
        createdTournament.setSize(tournament.getSize());
        createdTournament.setNoOfRounds(tournament.getNoOfRounds());

        return tournamentRepository.save(createdTournament);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> getUsersNotInCurrentTournament(Long tournamentId) throws Exception {
        List<User> userList = userRepository.findAll();
        List<User> nonParticipatingUsers = new ArrayList<>();
        for (User user : userList) {
            Tournament tournament = findTournamentById(tournamentId);
            if (isNonParticipatingUser(user, tournament)) {
                nonParticipatingUsers.add(user);
            }
        }
        return Optional.ofNullable(nonParticipatingUsers).orElseGet(ArrayList::new);
    }

    /**
     * Helper method to check if a user is not a participant in a tournament
     * @param user the user
     * @param tournament the tournament
     * @return false if the user has no role, is an admin or is already a participant in the tournament, true otherwise
     */
    private boolean isNonParticipatingUser(User user, Tournament tournament) {
        if(user.getRole() != null && !user.getRole().equals("ROLE_ADMIN") && !user.getCurrentTournaments().contains(tournament)) {
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tournament> getTournamentsSortedByName() throws Exception {
        List<Tournament> list = getAllTournament();
        Collections.sort(list, new Comparator<Tournament>() {
            @Override
            public int compare(Tournament t1, Tournament t2) {
                return t1.getTournament_name().compareTo(t2.getTournament_name());
            }
        });
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tournament> getTournamentsSortedByDate() throws Exception {
        List<Tournament> list = getAllTournament();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Collections.sort(list, new Comparator<Tournament>() {
            @Override
            public int compare(Tournament t1, Tournament t2) {
                try {
                    Date date1 = dateFormat.parse(t1.getDate());
                    Date date2 = dateFormat.parse(t2.getDate());
                    return date2.compareTo(date1); // Sort in descending order
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tournament> getTournamentsSortedBySize() throws Exception {
        List<Tournament> list = getAllTournament();
        Collections.sort(list, new Comparator<Tournament>() {
            @Override
            public int compare(Tournament t1, Tournament t2) {
                int availableSlots1 = t1.getSize() - t1.getCurrentSize();
                int availableSlots2 = t2.getSize() - t2.getCurrentSize();
                return Integer.compare(availableSlots2, availableSlots1); // Sort in descending order
            }
        });
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<TournamentDTO> findAllTournamentsDTO() throws Exception {
        List<Tournament> tournaments = tournamentRepository.findAll();
        return TournamentMapper.toDTOList(tournaments);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tournament startTournament(Long id) throws Exception {
        Tournament currentTournament = findTournamentById(id);
        if (!currentTournament.getStatus().equals("active")) {
            throw new Exception("Tournament is ongoing or completed, cannot be started");
        }

        // Create the first round and add it to the tournament
        Round firstRound = roundService.createFirstRound(id);
        addRoundToTournament(currentTournament, firstRound);

        // Update the tournament status to ongoing
        currentTournament.setStatus("ongoing");
        
        // debugging purposes
        logger.info("Tournament started");

        return tournamentRepository.save(currentTournament);
    }

    /**
     * Helper method to add a round to a tournament
     * @param currentTournament
     * @param firstRound
     */
    private void addRoundToTournament(Tournament currentTournament, Round round) {
        List<Round> rounds = currentTournament.getRounds();
        rounds.add(round);
        currentTournament.setRounds(rounds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkComplete(Long tournamentId) throws Exception {
        // debugging purposes
        logger.info("Tournament Check complete called");

        Tournament currentTournament = findTournamentById(tournamentId);

        // check if there are already the number of rounds specified
        if (currentTournament.getRounds().size() == currentTournament.getNoOfRounds()) {
            // invoke endTournament, which will update the tournament status to completed
            // and get the final rankings (tiebreak if necessary)
            endTournament(tournamentId);
        } else {
            // create the next round and add it to the tournament
            currentTournament.setCurrentRound(currentTournament.getCurrentRound() + 1);
            Round nextRound = roundService.createNextRound(tournamentId);
            addRoundToTournament(currentTournament, nextRound);
            tournamentRepository.save(currentTournament);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endTournament(Long tournamentId) throws Exception {
        // debugging purposes
        logger.info("End tournament called");

        Tournament currentTournament = findTournamentById(tournamentId);

        currentTournament.setStatus("completed");
        tournamentRepository.save(currentTournament);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void deleteTournament(Long id) throws Exception {
        // Remove all participants from the tournament
        removeAllUsers(id);

        // Now delete the tournament
        tournamentRepository.deleteById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAllUsers(Long id) throws Exception {
        Tournament tournament = findTournamentById(id);

        // Remove all participants from the tournament
        List<User> participants = new ArrayList<>(tournament.getParticipants());
        for (User user : participants) {
            removeUserFromTournament(tournament, user);
        }

        // Save the tournament to update the changes in the database
        tournamentRepository.save(tournament);
    }
}
