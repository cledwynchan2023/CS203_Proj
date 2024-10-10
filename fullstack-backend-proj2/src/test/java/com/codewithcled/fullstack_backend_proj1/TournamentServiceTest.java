package com.codewithcled.fullstack_backend_proj1;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.codewithcled.fullstack_backend_proj1.DTO.CreateTournamentRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.TournamentDTO;
import com.codewithcled.fullstack_backend_proj1.DTO.TournamentMapper;
import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.repository.TournamentRepository;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;
import com.codewithcled.fullstack_backend_proj1.service.TournamentServiceImplementation;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TournamentServiceTest {

    @InjectMocks
    private TournamentServiceImplementation tournamentService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TournamentRepository tournamentRepository;

    @Test
    void getAllTournament_Success_returnListTournament() {
        List<Tournament> resultList = new ArrayList<Tournament>();
        when(tournamentRepository.findAll()).thenReturn(resultList);

        List<Tournament> result = tournamentService.getAllTournament();

        assertIterableEquals(resultList, result);
        verify(tournamentRepository).findAll();
    }

    /*
     * public void findTournamentByName(){
     * String name="test";
     * when(tournamentRepository.findByTournamentName(name)).thenReturn(new
     * Tournament());
     * 
     * Tournament result = tournamentService.findTournamentByName(name);
     * 
     * assertNotNull(result);
     * verify(tournamentRepository).findByTournamentName(name);
     * }
     */

    @Test
    void getTournamentParticipants_Success_returnListUser() {
        Long id = (long) 500;
        Tournament testTournament = new Tournament();
        User testUser = new User();
        testUser.setUsername("testUser");
        testUser.setId(id);
        List<User> testUserList = new ArrayList<User>();
        testUserList.add(testUser);
        testTournament.setParticipants(testUserList);
        Optional<Tournament> returnT = Optional.of(testTournament);
        when(tournamentRepository.findById(id)).thenReturn(returnT);

        try {
            List<User> result = tournamentService.getTournamentParticipants(id);
            assertNotNull(result);
            assertIterableEquals(result, testUserList);
            verify(tournamentRepository).findById(id);
        } catch (Exception e) {
        }
    }

    @Test
    void getTournamentParticipants_failure_returnError() {
        Long id = (long) 500;
        Tournament testTournament = new Tournament();
        User testUser = new User();
        testUser.setUsername("testUser");
        testUser.setId(id);
        Optional<Tournament> returnT = Optional.of(testTournament);
        when(tournamentRepository.findById(id)).thenReturn(returnT);

        try {
            tournamentService.getTournamentParticipants(id);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Error Occured");
            verify(tournamentRepository).findById(id);
        }
    }

    @Test
    void updateUserParticipating_Success_returnTournament() {
        Long uIdF = (long) 10;
        Long uIdT = (long) 11;
        Long tId = (long) 11;
        Tournament testTournament = new Tournament();
        User firstUser = new User();
        firstUser.setUsername("FirstUser");
        firstUser.setId(uIdF);
        testTournament.addParticipant(firstUser);
        User testUser = new User();
        testUser.setId(uIdT);
        testUser.setUsername("TestUser");
        Optional<Tournament> returnTournament = Optional.of(testTournament);

        List<User> finalParticipantList = new ArrayList<User>();
        finalParticipantList.add(firstUser);
        finalParticipantList.add(testUser);

        when(tournamentRepository.findById(tId)).thenReturn(returnTournament);

        try {

            Tournament result = tournamentService.updateUserParticipating(uIdT, tId);

            assertIterableEquals(finalParticipantList, result.getParticipants());
            verify(tournamentRepository).findById(tId);

        } catch (Exception e) {

        }
    }

    @Test
    void updateUserParticipating_Failure_NoTournament_returnError() {
        Long uIdF = (long) 10;
        Long uIdT = (long) 11;
        Long tId = (long) 11;
        User firstUser = new User();
        firstUser.setUsername("FirstUser");
        firstUser.setId(uIdF);
        User testUser = new User();
        testUser.setId(uIdT);
        testUser.setUsername("TestUser");
        Optional<Tournament> returnTournament = Optional.empty();
        List<User> finalParticipantList = new ArrayList<User>();
        finalParticipantList.add(firstUser);
        finalParticipantList.add(testUser);

        when(tournamentRepository.findById(tId)).thenReturn(returnTournament);

        try {

            tournamentService.updateUserParticipating(uIdT, tId);

        } catch (Exception e) {
            assertEquals("Tournament not found", e.getMessage());
            verify(tournamentRepository).findById(tId);
            verify(userRepository, never()).findById(uIdT);
        }
    }

    @Test
    void updateUserParticipating_Failure_NoUser_returnError() {
        Long uIdF = (long) 10;
        Long uIdT = (long) 11;
        Long tId = (long) 11;
        Tournament testTournament = new Tournament();
        User firstUser = new User();
        firstUser.setUsername("FirstUser");
        firstUser.setId(uIdF);
        testTournament.addParticipant(firstUser);
        Optional<Tournament> returnTournament = Optional.of(testTournament);
        Optional<User> returnUser = Optional.empty();

        when(tournamentRepository.findById(tId)).thenReturn(returnTournament);
        when(userRepository.findById(uIdT)).thenReturn(returnUser);
        try {

            tournamentService.updateUserParticipating(uIdT, tId);

        } catch (Exception e) {
            assertEquals("User not found", e.getMessage());
            verify(tournamentRepository).findById(tId);
            verify(userRepository).findById(uIdT);
        }
    }

    @Test
    void removeUserParticipating_Success_ReturnTournament() {
        Long uId = (long) 10;
        Long tId = (long) 11;
        Tournament testTournament = new Tournament();
        User firstUser = new User();
        firstUser.setUsername("FirstUser");
        testTournament.addParticipant(new User());
        Optional<Tournament> returnTournament = Optional.of(testTournament);

        List<User> finalParticipantList = new ArrayList<User>();

        when(tournamentRepository.findById(tId)).thenReturn(returnTournament);
        try {

            Tournament result = tournamentService.removeUserParticipating(uId, tId);

            assertIterableEquals(finalParticipantList, result.getParticipants());
            verify(tournamentRepository).findById(tId);
        } catch (Exception e) {

        }
    }

    @Test
    void removeUserParticipating_Failure_TournamentNotFound_ReturnError() {
        Long uId = (long) 10;
        Long tId = (long) 11;
        Tournament testTournament = new Tournament();
        User firstUser = new User();
        firstUser.setUsername("FirstUser");
        testTournament.addParticipant(new User());
        Optional<Tournament> returnTournament = Optional.empty();

        when(tournamentRepository.findById(tId)).thenReturn(returnTournament);
        try {

            tournamentService.removeUserParticipating(uId, tId);

        } catch (Exception e) {
            assertEquals("Tournament not found", e.getMessage());
            verify(tournamentRepository).findById(tId);
        }
    }

    @Test
    void removeUserParticipating_Failure_UserNotFound_ReturnError() {
        Long uId = (long) 10;
        Long tId = (long) 11;
        Tournament testTournament = new Tournament();
        User firstUser = new User();
        firstUser.setUsername("FirstUser");
        testTournament.addParticipant(new User());
        Optional<Tournament> returnTournament = Optional.of(testTournament);
        Optional<User> returnUser = Optional.empty();

        when(tournamentRepository.findById(tId)).thenReturn(returnTournament);
        when(userRepository.findById(uId)).thenReturn(returnUser);
        try {

            tournamentService.removeUserParticipating(uId, tId);

        } catch (Exception e) {
            assertEquals("User not found", e.getMessage());
            verify(tournamentRepository).findById(tId);
            verify(userRepository).findById(uId);
        }
    }

    @Test
    void removeUserParticipating_Failure_UserNotInTournament_ReturnError() {
        Long uId = (long) 10;
        Long tId = (long) 11;
        Tournament testTournament = new Tournament();
        User firstUser = new User();
        firstUser.setUsername("FirstUser");
        Optional<Tournament> returnTournament = Optional.of(testTournament);
        Optional<User> returnUser = Optional.of(firstUser);

        when(tournamentRepository.findById(tId)).thenReturn(returnTournament);
        when(userRepository.findById(uId)).thenReturn(returnUser);
        try {

            tournamentService.removeUserParticipating(uId, tId);

        } catch (Exception e) {
            assertEquals("User is not participating in the tournament", e.getMessage());
            verify(tournamentRepository).findById(tId);
            verify(userRepository).findById(uId);
        }
    }

    @Test
    void updateTournament_Success_ReturnTournament() {
        Long tId = (long) 11;
        CreateTournamentRequest tournamentUpdateData = new CreateTournamentRequest();
        tournamentUpdateData.setTournament_name("newName");
        Tournament originalTournament = new Tournament();
        originalTournament.setTournament_name("oldName");
        Optional<Tournament> returnTournament = Optional.of(originalTournament);

        when(tournamentRepository.findById(tId)).thenReturn(returnTournament);
        when(tournamentRepository.save(originalTournament)).thenReturn(originalTournament);

        try {

            Tournament result = tournamentService.updateTournament(tId, tournamentUpdateData);

            assertEquals(tournamentUpdateData.getTournament_name(), result.getTournament_name());
            verify(tournamentRepository).save(originalTournament);

        } catch (Exception e) {

        }

    }

    @Test
    void updateTournament_Failure_TournamentNotFound_ReturnError() {
        Long tId = (long) 11;
        CreateTournamentRequest tournamentUpdateData = new CreateTournamentRequest();
        tournamentUpdateData.setTournament_name("newName");
        Tournament originalTournament = new Tournament();
        originalTournament.setTournament_name("oldName");
        Optional<Tournament> returnTournament = Optional.of(originalTournament);

        when(tournamentRepository.findById(tId)).thenReturn(returnTournament);
        when(tournamentRepository.save(originalTournament)).thenReturn(originalTournament);

        try {

            tournamentService.updateTournament(tId, tournamentUpdateData);

        } catch (Exception e) {
            assertEquals("Tournament not found", e.getMessage());
            verify(tournamentRepository).findById(tId);
        }

    }

    @Test
    void getTournamentsWithNoCurrentUser_Success_ReturnTournamentList() {
        Long uId = (long) 10;
        Long tId = (long) 11;
        User testUser = new User();
        testUser.setId(uId);
        List<Tournament> resultList = new ArrayList<Tournament>();
        Tournament testTournament = new Tournament();
        testTournament.setId(tId);
        testTournament.setTournament_name("testTournament");
        resultList.add(testTournament);

        when(tournamentRepository.findAll()).thenReturn(resultList);
        try {
            List<Tournament> result = tournamentService.getTournamentsWithNoCurrentUser(uId);

            assertIterableEquals(resultList, result);
            verify(tournamentRepository).findAll();
        } catch (Exception e) {

        }

    }

    @Test
    void getTournamentsWithNoCurrentUser_Failure_UserNotFound_ReturnError() {
        Long uId = (long) 10;
        Long tId = (long) 11;
        User testUser = new User();
        testUser.setId(uId);
        List<Tournament> resultList = new ArrayList<Tournament>();
        Tournament testTournament = new Tournament();
        testTournament.setId(tId);
        testTournament.setTournament_name("testTournament");
        resultList.add(testTournament);
        Optional<User> returnUser = Optional.empty();

        when(tournamentRepository.findAll()).thenReturn(resultList);
        when(userRepository.findById(uId)).thenReturn(returnUser);
        try {
            tournamentService.getTournamentsWithNoCurrentUser(uId);

        } catch (Exception e) {
            assertEquals("User not found", e.getMessage());
            verify(tournamentRepository).findAll();
            verify(userRepository).findById(uId);
        }

    }

    @Test
    void createTournament_Success_ReturnTournament() {
        String tournament_name = "Test tournament";
        String date = "20/10/2201";
        String status = "Active";
        int size = 0;
        int noOfRounds = 0;

        CreateTournamentRequest tournamentCreateData = new CreateTournamentRequest();
        tournamentCreateData.setTournament_name(tournament_name);
        tournamentCreateData.setDate(date);
        tournamentCreateData.setStatus(status);
        tournamentCreateData.setSize(size);
        tournamentCreateData.setNoOfRounds(noOfRounds);

        Tournament returnTournament = new Tournament();
        returnTournament.setTournament_name(tournament_name);
        returnTournament.setDate(date);
        returnTournament.setActive(status);
        returnTournament.setSize(size);
        returnTournament.setNoOfRounds(noOfRounds);

        when(tournamentRepository.save(returnTournament)).thenReturn(returnTournament);

        try {

            Tournament result = tournamentService.createTournament(tournamentCreateData);

            assertEquals(tournament_name, result.getTournament_name());
            assertEquals(date, result.getDate());
            assertEquals(status, result.getStatus());
            assertEquals(size, result.getSize());
            assertEquals(noOfRounds, result.getNoOfRounds());
            verify(tournamentRepository).save(returnTournament);

        } catch (Exception e) {

        }
    }

    @Test
    void getNonParticipatingCurrentUser_Success_ReturnUserList() {
        Long tId = (long) 11;
        Long uId1 = (long) 10;
        Long uId2 = (long) 11;

        Tournament testTournament = new Tournament();
        testTournament.setId(tId);
        List<Tournament> tournamentList = new ArrayList<Tournament>();
        tournamentList.add(testTournament);

        User tUser1 = new User();
        tUser1.setRole("ROLE_USER");
        tUser1.setUsername("tUser1");
        tUser1.setCurrentTournaments(tournamentList);
        tUser1.setId(uId1);

        User tUser2 = new User();
        tUser2.setRole("ROLE_USER");
        tUser2.setUsername("tUser2");
        tUser2.setId(uId2);

        List<User> userList = new ArrayList<User>();
        userList.add(tUser1);
        userList.add(tUser2);

        Optional<Tournament> returnTournament = Optional.of(testTournament);
        when(tournamentRepository.findById(tId)).thenReturn(returnTournament);
        when(userRepository.findAll()).thenReturn(userList);
        try {
            List<User> result = tournamentService.getNonParticipatingCurrentUser(tId);

            assertEquals(1, result.size());
            assertEquals(tUser2, result.get(0));
            verify(tournamentRepository).findById(tId);
            verify(userRepository).findAll();
        } catch (Exception e) {

        }
    }

    @Test
    void getNonParticipatingCurrentUser_Failure_TournamentNotFound_ReturnException() {
        Long tId = (long) 11;
        Long uId1 = (long) 10;
        Long uId2 = (long) 11;

        Tournament testTournament = new Tournament();
        List<Tournament> tournamentList = new ArrayList<Tournament>();
        tournamentList.add(testTournament);

        User tUser1 = new User();
        tUser1.setRole("ROLE_USER");
        tUser1.setUsername("tUser1");
        tUser1.setCurrentTournaments(tournamentList);
        tUser1.setId(uId1);

        User tUser2 = new User();
        tUser2.setRole("ROLE_USER");
        tUser2.setUsername("tUser2");
        tUser2.setId(uId2);

        List<User> userList = new ArrayList<User>();
        userList.add(tUser1);
        userList.add(tUser2);

        Optional<Tournament> returnTournament = Optional.empty();
        when(tournamentRepository.findById(tId)).thenReturn(returnTournament);
        try {

            tournamentService.getNonParticipatingCurrentUser(tId);

        } catch (Exception e) {

            assertEquals("Tournament not found", e.getMessage());
            verify(tournamentRepository).findById(tId);
            verify(userRepository, never()).findAll();

        }
    }

    @Test
    void findAllTournamentsDTO_Success_ReturnTournamentDTOList() {
        Long tId = (long) 11;
        List<Tournament> tournamentList = new ArrayList<Tournament>();
        Tournament testTournament = new Tournament();
        testTournament.setTournament_name("test");
        testTournament.setId(tId);
        tournamentList.add(testTournament);

        List<TournamentDTO> tournamentDTOList = TournamentMapper.toDTOList(tournamentList);

        when(tournamentRepository.findAll()).thenReturn(tournamentList);

        try {
            List<TournamentDTO> result = tournamentService.findAllTournamentsDTO();

            assertEquals(tournamentDTOList.get(0).getTournamentName(), result.get(0).getTournamentName());
            verify(tournamentRepository).findAll();
            
        } catch (Exception e) {

        }

    }

}
