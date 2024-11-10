package com.codewithcled.fullstack_backend_proj1.UnitTests;

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
import com.codewithcled.fullstack_backend_proj1.model.Round;
import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.repository.RoundRepository;
import com.codewithcled.fullstack_backend_proj1.repository.TournamentRepository;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;
import com.codewithcled.fullstack_backend_proj1.service.RoundService;
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

    @Mock
    private RoundRepository roundRepository;

    @Mock
    private RoundService roundService;

    @Test
    void getAllTournament_Success_returnListTournament() {
        List<Tournament> resultList = new ArrayList<Tournament>();
        when(tournamentRepository.findAll()).thenReturn(resultList);

        List<Tournament> result = tournamentService.getAllTournament();

        assertIterableEquals(resultList, result);
        verify(tournamentRepository).findAll();
    }

    @Test
    void getTournamentParticipants_Success_returnListUser() throws Exception {
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

        List<User> result = tournamentService.getTournamentParticipants(id);
        assertNotNull(result);
        assertIterableEquals(result, testUserList);
        verify(tournamentRepository).findById(id);
    }

    @Test
    void getTournamentParticipants_failure_returnException() {
        Long id = (long) 500;

        when(tournamentRepository.findById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            tournamentService.getTournamentParticipants(id);
        });
        assertEquals("Error Occured", exception.getMessage());

        verify(tournamentRepository).findById(id);

    }

    @Test
    void updateUserParticipating_Success_returnTournament() throws Exception {
        Long uIdF = (long) 10;
        Long uIdT = (long) 11;
        Long tId = (long) 11;
        Tournament testTournament = new Tournament();
        testTournament.setSize(5);
        testTournament.setCurrentSize(1);
        User firstUser = new User();
        firstUser.setUsername("FirstUser");
        firstUser.setId(uIdF);
        testTournament.addParticipant(firstUser);
        User testUser = new User();
        testUser.setId(uIdT);
        testUser.setUsername("TestUser");
        Optional<Tournament> returnTournament = Optional.of(testTournament);

        List<User> finalParticipantList = new ArrayList<User>();
        List<Tournament> finalTournamentList = new ArrayList<Tournament>();
        finalParticipantList.add(firstUser);
        finalParticipantList.add(testUser);
        finalTournamentList.add(testTournament);
        Optional<User> returnUser = Optional.of(testUser);

        when(tournamentRepository.findById(tId)).thenReturn(returnTournament);
        when(userRepository.findById(uIdT)).thenReturn(returnUser);
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(tournamentRepository.save(testTournament)).thenReturn(testTournament);

        Tournament result = tournamentService.updateUserParticipating(uIdT, tId);

        assertIterableEquals(finalParticipantList, result.getParticipants());
        assertIterableEquals(finalTournamentList, testUser.getCurrentTournaments());
        verify(tournamentRepository).findById(tId);
        verify(userRepository).findById(uIdT);
        verify(userRepository).save(testUser);
        verify(tournamentRepository).save(testTournament);

    }

    @Test
    void updateUserParticipating_Success_ParticipantAlreadyInside() throws Exception {
        Long uIdT = (long) 11;
        Long tId = (long) 11;
        Tournament testTournament = new Tournament();
        testTournament.setSize(5);
        testTournament.setCurrentSize(1);
    
        User testUser = new User();
        testUser.setId(uIdT);
        testUser.setUsername("TestUser");
        testTournament.addParticipant(testUser);
        testUser.setCurrentTournaments(List.of(testTournament));

        List<User> finalParticipantList = new ArrayList<User>();
        List<Tournament> finalTournamentList = new ArrayList<Tournament>();
        finalParticipantList.add(testUser);
        finalTournamentList.add(testTournament);

        when(tournamentRepository.findById(tId)).thenReturn(Optional.of(testTournament));
        when(userRepository.findById(uIdT)).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(tournamentRepository.save(testTournament)).thenReturn(testTournament);

        Tournament result = tournamentService.updateUserParticipating(uIdT, tId);

        assertIterableEquals(finalParticipantList, result.getParticipants());
        assertIterableEquals(finalTournamentList, testUser.getCurrentTournaments());
        verify(tournamentRepository).findById(tId);
        verify(userRepository).findById(uIdT);
        verify(userRepository).save(testUser);
        verify(tournamentRepository).save(testTournament);

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
        List<User> finalParticipantList = new ArrayList<User>();
        finalParticipantList.add(firstUser);
        finalParticipantList.add(testUser);

        when(tournamentRepository.findById(tId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            tournamentService.updateUserParticipating(uIdT, tId);
        });
        assertEquals("Tournament not found", exception.getMessage());

        verify(tournamentRepository).findById(tId);
        verify(userRepository, never()).findById(uIdT);
    }

    @Test
    void updateUserParticipating_Failure_NoUser_returnException() {
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

        Exception exception = assertThrows(Exception.class, () -> {
            tournamentService.updateUserParticipating(uIdT, tId);
        });
        assertEquals("User not found", exception.getMessage());

        verify(tournamentRepository).findById(tId);
        verify(userRepository).findById(uIdT);
    }

    @Test
    void updateUserParticipating_Failure_TournamentAtMaxSize_returnException() {
        Long uIdF = (long) 10;
        Long uIdT = (long) 11;
        Long tId = (long) 11;
        Tournament testTournament = new Tournament();
        testTournament.setSize(5);
        testTournament.setCurrentSize(5);
        User firstUser = new User();
        firstUser.setUsername("FirstUser");
        firstUser.setId(uIdF);
        testTournament.addParticipant(firstUser);
        User testUser = new User();
        testUser.setId(uIdT);
        testUser.setUsername("TestUser");

        when(tournamentRepository.findById(tId)).thenReturn(Optional.of(testTournament));
        when(userRepository.findById(uIdT)).thenReturn(Optional.of(testUser));

        Exception exception = assertThrows(Exception.class, () -> {
            tournamentService.updateUserParticipating(uIdT, tId);
        });

        assertEquals("Tournament is full", exception.getMessage());
        verify(tournamentRepository).findById(tId);
        verify(userRepository).findById(uIdT);
    }

    @Test
    void removeUserParticipating_Success_ReturnTournament() throws Exception {
        Long uId = (long) 10;
        Long tId = (long) 11;
        Tournament testTournament = new Tournament();
        testTournament.setSize(5);
        testTournament.setCurrentSize(1);
        List<Tournament> tournamentList = new ArrayList<Tournament>();
        tournamentList.add(testTournament);
        User firstUser = new User();
        firstUser.setUsername("FirstUser");
        firstUser.setCurrentTournaments(tournamentList);
        testTournament.addParticipant(firstUser);
        Optional<Tournament> returnTournament = Optional.of(testTournament);
        Optional<User> returnUser = Optional.of(firstUser);

        List<User> finalParticipantList = new ArrayList<User>();
        List<Tournament> finalTournamentList = new ArrayList<Tournament>();

        when(tournamentRepository.findById(tId)).thenReturn(returnTournament);
        when(userRepository.findById(uId)).thenReturn(returnUser);
        when(userRepository.save(firstUser)).thenReturn(firstUser);
        when(tournamentRepository.save(testTournament)).thenReturn(testTournament);

        Tournament result = tournamentService.removeUserParticipating(uId, tId);

        assertIterableEquals(finalParticipantList, result.getParticipants());
        assertIterableEquals(finalTournamentList, firstUser.getCurrentTournaments());
        verify(tournamentRepository).findById(tId);
        verify(userRepository).findById(uId);
        verify(userRepository).save(firstUser);
        verify(tournamentRepository).save(testTournament);
    }

    @Test
    void removeUserParticipating_Failure_TournamentNotFound_ReturnException() {
        Long uId = (long) 10;
        Long tId = (long) 11;
        Tournament testTournament = new Tournament();
        User firstUser = new User();
        firstUser.setUsername("FirstUser");
        testTournament.addParticipant(new User());

        when(tournamentRepository.findById(tId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            tournamentService.removeUserParticipating(uId, tId);
        });

        assertEquals("Tournament not found", exception.getMessage());
        verify(tournamentRepository).findById(tId);
    }

    @Test
    void removeUserParticipating_Failure_UserNotFound_ReturnException() {
        Long uId = (long) 10;
        Long tId = (long) 11;
        Tournament testTournament = new Tournament();
        User firstUser = new User();
        firstUser.setUsername("FirstUser");
        testTournament.addParticipant(new User());

        when(tournamentRepository.findById(tId)).thenReturn(Optional.of(testTournament));
        when(userRepository.findById(uId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            tournamentService.removeUserParticipating(uId, tId);
        });

        assertEquals("User not found", exception.getMessage());

        verify(tournamentRepository).findById(tId);
        verify(userRepository).findById(uId);
    }

    @Test
    void removeUserParticipating_Failure_UserNotInTournament_ReturnException() {
        Long uId = (long) 10;
        Long tId = (long) 11;
        Tournament testTournament = new Tournament();
        User firstUser = new User();
        firstUser.setUsername("FirstUser");

        when(tournamentRepository.findById(tId)).thenReturn(Optional.of(testTournament));
        when(userRepository.findById(uId)).thenReturn(Optional.of(firstUser));

        Exception exception = assertThrows(Exception.class, () -> {
            tournamentService.removeUserParticipating(uId, tId);
        });

        assertEquals("User is not participating in the tournament", exception.getMessage());
        verify(tournamentRepository).findById(tId);
        verify(userRepository).findById(uId);

    }

    @Test
    void updateTournament_Success_ReturnTournament() throws Exception {
        Long tId = (long) 11;
        CreateTournamentRequest tournamentUpdateData = new CreateTournamentRequest();
        tournamentUpdateData.setTournament_name("newName");
        tournamentUpdateData.setDate("10/22/2011");
        tournamentUpdateData.setSize(5);
        tournamentUpdateData.setStatus("completed");
        tournamentUpdateData.setCurrentSize(1);
        tournamentUpdateData.setNoOfRounds(4);

        Tournament originalTournament = new Tournament();
        originalTournament.setTournament_name("oldName");
        originalTournament.setDate("11/22/2011");
        originalTournament.setSize(4);
        originalTournament.setStatus("active");
        originalTournament.setNoOfRounds(3);

        when(tournamentRepository.findById(tId)).thenReturn(Optional.of(originalTournament));
        when(tournamentRepository.save(originalTournament)).thenReturn(originalTournament);

        Tournament result = tournamentService.updateTournament(tId, tournamentUpdateData);

        assertEquals(tournamentUpdateData.getTournament_name(), result.getTournament_name());
        assertEquals(tournamentUpdateData.getDate(), result.getDate());
        assertEquals(tournamentUpdateData.getSize(), result.getSize());
        assertEquals(tournamentUpdateData.getStatus(), result.getStatus());
        assertEquals(tournamentUpdateData.getNoOfRounds(), result.getNoOfRounds());
        verify(tournamentRepository).findById(tId);
        verify(tournamentRepository).save(originalTournament);

    }

    @Test
    void updateTournament_Success_AllNull() throws Exception {
        Long tId = (long) 11;
        CreateTournamentRequest tournamentUpdateData = new CreateTournamentRequest();
        tournamentUpdateData.setCurrentSize(null);
        tournamentUpdateData.setNoOfRounds(null);

        Tournament originalTournament = new Tournament();
        originalTournament.setTournament_name("oldName");
        originalTournament.setDate("11/22/2011");
        originalTournament.setSize(4);
        originalTournament.setStatus("active");
        originalTournament.setNoOfRounds(3);
        Optional<Tournament> returnTournament = Optional.of(originalTournament);

        when(tournamentRepository.findById(tId)).thenReturn(returnTournament);
        when(tournamentRepository.save(originalTournament)).thenReturn(originalTournament);

        Tournament result = tournamentService.updateTournament(tId, tournamentUpdateData);

        assertEquals(originalTournament.getTournament_name(), result.getTournament_name());
        assertEquals(originalTournament.getDate(), result.getDate());
        assertEquals(originalTournament.getSize(), result.getSize());
        assertEquals(originalTournament.getStatus(), result.getStatus());
        assertEquals(originalTournament.getNoOfRounds(), result.getNoOfRounds());
        verify(tournamentRepository).findById(tId);
        verify(tournamentRepository).save(originalTournament);

    }

    @Test
    void updateTournament_Failure_TournamentNotFound_ReturnException() {
        Long tId = (long) 11;
        CreateTournamentRequest tournamentUpdateData = new CreateTournamentRequest();
        tournamentUpdateData.setTournament_name("newName");
        Tournament originalTournament = new Tournament();
        originalTournament.setTournament_name("oldName");

        when(tournamentRepository.findById(tId)).thenReturn(Optional.empty());


        Exception exception = assertThrows(Exception.class, () -> {
            tournamentService.updateTournament(tId, tournamentUpdateData);
        });

        assertEquals("Tournament not found", exception.getMessage());
        verify(tournamentRepository).findById(tId);
    }

    @Test
    void getTournamentsWithNoCurrentUser_Success_ReturnTournamentList() throws Exception {
        Long uId = (long) 10;
        Long tId = (long) 11;
        User testUser = new User();
        testUser.setId(uId);
        List<Tournament> tournamentList = new ArrayList<Tournament>();
        Tournament testTournament = new Tournament();
        testTournament.setId(tId);
        testTournament.setTournament_name("testTournament");
        Tournament notInTournament=new Tournament();
        notInTournament.setId(tId+1);
        notInTournament.setTournament_name("NotInTournament");
        tournamentList.add(testTournament);
        tournamentList.add(notInTournament);

        when(tournamentRepository.findAll()).thenReturn(tournamentList);
        when(userRepository.findById(uId)).thenReturn(Optional.of(testUser));

        List<Tournament> result = tournamentService.getTournamentsCurrentUserNotIn(uId);

        assertIterableEquals(List.of(testTournament,notInTournament), result);
        verify(tournamentRepository).findAll();

    }

    @Test
    void getTournamentsWithNoCurrentUser_Success_DoNotReturnIfUserIsAlreadyParticipant() throws Exception {
        Long uId = (long) 10;
        Long tId = (long) 11;
        User testUser = new User();
        testUser.setId(uId);
        List<Tournament> tournamentList = new ArrayList<Tournament>();
        Tournament testTournament = new Tournament();
        testTournament.setId(tId);
        testTournament.setTournament_name("testTournament");
        testTournament.addParticipant(testUser);

        Tournament notInTournament=new Tournament();
        notInTournament.setId(tId+1);
        notInTournament.setTournament_name("NotInTournament");
        tournamentList.add(testTournament);
        tournamentList.add(notInTournament);

        when(tournamentRepository.findAll()).thenReturn(tournamentList);
        when(userRepository.findById(uId)).thenReturn(Optional.of(testUser));

        List<Tournament> result = tournamentService.getTournamentsCurrentUserNotIn(uId);

        assertIterableEquals(List.of(notInTournament), result);
        verify(tournamentRepository).findAll();

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

        when(tournamentRepository.findAll()).thenReturn(resultList);
        when(userRepository.findById(uId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            tournamentService.getTournamentsCurrentUserNotIn(uId);
        });

        assertEquals("User not found", exception.getMessage());
        verify(tournamentRepository).findAll();
        verify(userRepository).findById(uId);

    }

    @Test
    void createTournament_Success_ReturnTournament() throws Exception {
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
        returnTournament.setStatus(status);
        returnTournament.setSize(size);
        returnTournament.setNoOfRounds(noOfRounds);

        when(tournamentRepository.save(returnTournament)).thenReturn(returnTournament);

        Tournament result = tournamentService.createTournament(tournamentCreateData);

        assertEquals(tournament_name, result.getTournament_name());
        assertEquals(date, result.getDate());
        assertEquals(status, result.getStatus());
        assertEquals(size, result.getSize());
        assertEquals(noOfRounds, result.getNoOfRounds());
        verify(tournamentRepository).save(returnTournament);
    }

    @Test
    void createTournament_Failure_OddSize_ReturnException() throws Exception {
        String tournament_name = "Test tournament";
        String date = "20/10/2201";
        String status = "Active";
        int size = 1;
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
        returnTournament.setStatus(status);
        returnTournament.setSize(size);
        returnTournament.setNoOfRounds(noOfRounds);

        Exception exception = assertThrows(Exception.class, () -> {
            tournamentService.createTournament(tournamentCreateData);
        });

        assertEquals("Tournament size must be even",exception.getMessage());
    }

    @Test
    void getNonParticipatingCurrentUser_Success_ReturnUserList() throws Exception {
        Long tId = (long) 11;
        Long uId1 = (long) 10;
        Long uId2 = (long) 11;

        Tournament testTournament = new Tournament();
        testTournament.setId(tId);
        Tournament testTournament2=new Tournament();
        testTournament2.setId(tId+1);
        List<Tournament> tournamentList = new ArrayList<Tournament>();
        tournamentList.add(testTournament);

        User tUser1 = new User();
        tUser1.setRole("ROLE_USER");
        tUser1.setUsername("tUser1");
        tUser1.setCurrentTournaments(tournamentList);
        tUser1.setId(uId1);

        User tUser2 = new User();
        tUser2.setRole("ROLE_USER");
        tUser2.setCurrentTournaments(List.of(testTournament2));
        tUser2.setUsername("tUser2");
        tUser2.setId(uId2);

        User tUser3 = new User();
        tUser3.setRole("ROLE_ADMIN");
        tUser3.setUsername("tADMIN");
        tUser3.setCurrentTournaments(tournamentList);
        tUser3.setId(uId2);

        User tUser4 = new User();
        tUser4.setUsername("tNULL");
        tUser4.setId(uId2);
        tUser4.setCurrentTournaments(tournamentList);

        List<User> userList = new ArrayList<User>();
        userList.add(tUser1);
        userList.add(tUser2);
        userList.add(tUser3);
        userList.add(tUser4);

        when(userRepository.findAll()).thenReturn(userList);

        List<User> result = tournamentService.getUsersNotInCurrentTournament(tId);

        assertEquals(1, result.size());
        assertEquals(tUser2, result.get(0));
        verify(userRepository).findAll();
    }

    @Test
    void getNonParticipatingCurrentUser_Success_NoTournamentID_returnAll() throws Exception {
        Long tId = (long) 11;
        Long uId1 = (long) 10;
        Long uId2 = (long) 11;

        List<Tournament> tournamentList = new ArrayList<Tournament>();

        User tUser1 = new User();
        tUser1.setRole("ROLE_USER");
        tUser1.setUsername("tUser1");
        tUser1.setCurrentTournaments(tournamentList);
        tUser1.setId(uId1);

        User tUser2 = new User();
        tUser2.setRole("ROLE_USER");
        tUser2.setUsername("tUser2");
        tUser2.setCurrentTournaments(tournamentList);
        tUser2.setId(uId2);

        List<User> userList = new ArrayList<User>();
        userList.add(tUser1);
        userList.add(tUser2);

        when(userRepository.findAll()).thenReturn(userList);

        List<User> result = tournamentService.getUsersNotInCurrentTournament(tId);

        assertIterableEquals(userList, result);

        verify(userRepository).findAll();
    }

    @Test
    void findAllTournamentsDTO_Success_ReturnTournamentDTOList() throws Exception {
        Long tId = (long) 11;
        List<Tournament> tournamentList = new ArrayList<Tournament>();
        Tournament testTournament = new Tournament();
        testTournament.setTournament_name("test");
        testTournament.setId(tId);
        tournamentList.add(testTournament);

        List<TournamentDTO> tournamentDTOList = TournamentMapper.toDTOList(tournamentList);

        when(tournamentRepository.findAll()).thenReturn(tournamentList);

        List<TournamentDTO> result = tournamentService.findAllTournamentsDTO();

        assertEquals(tournamentDTOList.get(0).getTournamentName(), result.get(0).getTournamentName());
        verify(tournamentRepository).findAll();
    }

    @Test
    void ActiveTournament_Success_ReturnListOfActiveTournaments() {
        Tournament testTournament = new Tournament();
        testTournament.setStatus("active");
        Tournament testTournament2 = new Tournament();
        testTournament2.setStatus("ongoing");
        List<Tournament> tournamentList = new ArrayList<Tournament>(List.of(testTournament, testTournament2));

        when(tournamentRepository.findAll()).thenReturn(tournamentList);

        List<Tournament> result = tournamentService.getActiveTournament();

        assertEquals(1, result.size());
        assertEquals("active", result.get(0).getStatus());
        verify(tournamentRepository).findAll();
    }

    @Test
    void OngoingTournament_Success_ReturnListOfOngoingTournaments() {
        Tournament testTournament = new Tournament();
        testTournament.setStatus("active");
        testTournament.setTournament_name("t1");
        Tournament testTournament2 = new Tournament();
        testTournament2.setStatus("ongoing");
        testTournament2.setTournament_name("t2");

        List<Tournament> tournamentList = new ArrayList<Tournament>(List.of(testTournament, testTournament2));

        when(tournamentRepository.findAll()).thenReturn(tournamentList);

        List<Tournament> result = tournamentService.getOngoingTournament();

        assertEquals(1, result.size());
        assertEquals("t2", result.get(0).getTournament_name());
    }

    @Test
    void CompletedTournament_Success_ReturnListOfCompletedTournaments() {
        Tournament testTournament = new Tournament();
        testTournament.setStatus("active");
        Tournament testTournament2 = new Tournament();
        testTournament2.setStatus("completed");
        List<Tournament> tournamentList = new ArrayList<Tournament>(List.of(testTournament, testTournament2));

        when(tournamentRepository.findAll()).thenReturn(tournamentList);

        List<Tournament> result = tournamentService.getCompletedTournament();

        assertEquals(1, result.size());
        assertEquals("completed", result.get(0).getStatus());
        verify(tournamentRepository).findAll();
    }

    @Test
    void FilteredTournamentsByName_Success_ReturnSortedTournamentListByName() throws Exception {
        Tournament testTournament1 = new Tournament();
        testTournament1.setTournament_name("t1");
        testTournament1.setDate("10/5/2024");
        testTournament1.setSize(4);
        testTournament1.setCurrentSize(3);
        Tournament testTournament2 = new Tournament();
        testTournament2.setTournament_name("t2");
        testTournament2.setDate("12/5/2024");
        testTournament2.setSize(4);
        testTournament2.setCurrentSize(1);
        Tournament testTournament3 = new Tournament();
        testTournament3.setTournament_name("t3");
        testTournament3.setDate("13/5/2024");
        testTournament3.setSize(4);
        testTournament3.setCurrentSize(2);

        List<Tournament> tournamentList = new ArrayList<>();
        tournamentList.add(testTournament3);
        tournamentList.add(testTournament1);
        tournamentList.add(testTournament2);

        when(tournamentRepository.findAll()).thenReturn(tournamentList);

        List<Tournament> result = tournamentService.getTournamentsSortedByName();

        assertEquals(3, result.size());
        assertEquals("t1", result.get(0).getTournament_name());
        assertEquals("t2", result.get(1).getTournament_name());
        assertEquals("t3", result.get(2).getTournament_name());

        verify(tournamentRepository).findAll();
    }

    @Test
    void FilteredTournamentsByDate_Success_ReturnSortedTournamentListByDate() throws Exception {
        Tournament testTournament1 = new Tournament();
        testTournament1.setTournament_name("t1");
        testTournament1.setDate("10/5/2024");
        testTournament1.setSize(4);
        testTournament1.setCurrentSize(3);

        Tournament testTournament2 = new Tournament();
        testTournament2.setTournament_name("t2");
        testTournament2.setDate("12/5/2024");
        testTournament2.setSize(4);
        testTournament2.setCurrentSize(1);

        Tournament testTournament3 = new Tournament();
        testTournament3.setTournament_name("t3");
        testTournament3.setDate("13/5/2024");
        testTournament3.setSize(4);
        testTournament3.setCurrentSize(2);

        List<Tournament> tournamentList = new ArrayList<>();
        tournamentList.add(testTournament3);
        tournamentList.add(testTournament1);
        tournamentList.add(testTournament2);

        when(tournamentRepository.findAll()).thenReturn(tournamentList);

        List<Tournament> result = tournamentService.getTournamentsSortedByDate();

        assertEquals(3, result.size());
        assertEquals("t3", result.get(0).getTournament_name()); // Most recent date
        assertEquals("t2", result.get(1).getTournament_name());
        assertEquals("t1", result.get(2).getTournament_name()); // Oldest date

        verify(tournamentRepository).findAll();
    }

    @Test
    void FilteredTournamentsByDate_Failure_OneTournamentHasIncorrectDateFormat_ReturnException() throws Exception {
        Tournament testTournament1 = new Tournament();
        testTournament1.setTournament_name("t1");
        testTournament1.setDate("10/5/2024");
        testTournament1.setSize(4);
        testTournament1.setCurrentSize(3);

        Tournament testTournament2 = new Tournament();
        testTournament2.setTournament_name("t2");
        testTournament2.setDate("1252024");//Wrong Date Format input
        testTournament2.setSize(4);
        testTournament2.setCurrentSize(1);

        Tournament testTournament3 = new Tournament();
        testTournament3.setTournament_name("t3");
        testTournament3.setDate("13/5/2024");
        testTournament3.setSize(4);
        testTournament3.setCurrentSize(2);

        List<Tournament> tournamentList = new ArrayList<>();
        tournamentList.add(testTournament3);
        tournamentList.add(testTournament1);
        tournamentList.add(testTournament2);

        when(tournamentRepository.findAll()).thenReturn(tournamentList);

        Exception exception = assertThrows(Exception.class, () -> {
            tournamentService.getTournamentsSortedByDate();
        });

        assertEquals("java.text.ParseException: Unparseable date: \"1252024\"", exception.getMessage());

        verify(tournamentRepository).findAll();
    }

    @Test
    void getFilteredTournamentsBySize_Success_ReturnTournamentListSortedByOpenSlots() throws Exception {
        // Comparing by availiable slots not just size
        Tournament testTournament1 = new Tournament();
        testTournament1.setTournament_name("t1");
        testTournament1.setDate("10/5/2024");
        testTournament1.setSize(4);
        testTournament1.setCurrentSize(3);

        Tournament testTournament2 = new Tournament();
        testTournament2.setTournament_name("t2");
        testTournament2.setDate("12/5/2024");
        testTournament2.setSize(4);
        testTournament2.setCurrentSize(1);

        Tournament testTournament3 = new Tournament();
        testTournament3.setTournament_name("t3");
        testTournament3.setDate("13/5/2024");
        testTournament3.setSize(4);
        testTournament3.setCurrentSize(2);

        List<Tournament> tournamentList = new ArrayList<>();
        tournamentList.add(testTournament3);
        tournamentList.add(testTournament1);
        tournamentList.add(testTournament2);

        when(tournamentRepository.findAll()).thenReturn(tournamentList);

        List<Tournament> result = tournamentService.getTournamentsSortedBySize();

        assertEquals(3, result.size());
        assertEquals("t2", result.get(0).getTournament_name());
        assertEquals("t3", result.get(1).getTournament_name());
        assertEquals("t1", result.get(2).getTournament_name());
        verify(tournamentRepository).findAll();
    }

    @Test
    void startTournament_Success_ReturnUpdatedTournament() throws Exception {
        Long tId = (long) 11;
        int roundNo = 2;
        Tournament testTournament = new Tournament();
        testTournament.setTournament_name("test");
        testTournament.setId(tId);
        testTournament.setStatus("active");
        testTournament.setNoOfRounds(roundNo);
        List<Round> rounds = new ArrayList<Round>();
        testTournament.setRounds(rounds);

        Round testRound = new Round();

        when(tournamentRepository.findById(tId)).thenReturn(Optional.of(testTournament));
        when(roundService.createFirstRound(tId)).thenReturn(testRound);
        when(tournamentRepository.save(testTournament)).thenReturn(testTournament);

        Tournament result = tournamentService.startTournament(tId);

        assertEquals(1, result.getRounds().size());
        assertEquals("ongoing", result.getStatus());
        verify(tournamentRepository).findById(tId);
        verify(roundService).createFirstRound(tId);
        verify(tournamentRepository).save(testTournament);
    }

    @Test
    void startTournament_Failure_TournamentNotActive_ReturnException() throws Exception {
        Long tId = (long) 11;
        int roundNo = 2;
        Tournament testTournament = new Tournament();
        testTournament.setTournament_name("test");
        testTournament.setId(tId);
        testTournament.setStatus("ongoing");
        testTournament.setNoOfRounds(roundNo);
        List<Round> rounds = new ArrayList<Round>();
        testTournament.setRounds(rounds);

        when(tournamentRepository.findById(tId)).thenReturn(Optional.of(testTournament));
        
        Exception exception = assertThrows(Exception.class, () -> {
            tournamentService.startTournament(tId);
        });
        assertEquals("Tournament is ongoing or completed", exception.getMessage());

        verify(tournamentRepository).findById(tId);
    }

    @Test
    void startTournament_Failure_TournamentNotFound_ReturnException() throws Exception {
        Long tId = (long) 11;
        when(tournamentRepository.findById(tId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            tournamentService.startTournament(tId);
        });
        assertEquals("Tournament not found", exception.getMessage());

        verify(tournamentRepository).findById(tId);
    }

    @Test
    void checkComplete_Success_RoundsEqualNoOfRounds_EndTournament() throws Exception {
        Long tId = (long) 11;
        int roundNo = 1;
        Tournament testTournament = new Tournament();
        testTournament.setTournament_name("test");
        testTournament.setId(tId);
        testTournament.setStatus("active");
        testTournament.setNoOfRounds(roundNo);
        List<Round> rounds = new ArrayList<Round>(List.of(new Round()));
        testTournament.setRounds(rounds);

        when(tournamentRepository.findById(tId)).thenReturn(Optional.of(testTournament));
        when(tournamentRepository.save(testTournament)).thenReturn(testTournament);

        tournamentService.checkComplete(tId);

        assertEquals("completed", testTournament.getStatus());
        verify(tournamentRepository, times(2)).findById(tId);
        verify(tournamentRepository).save(testTournament);
    }

    @Test
    void checkComplete_Success_RoundsNotEqualNoOfRounds_AddNewRound() throws Exception {
        Long tId = (long) 11;
        int roundNo = 4;
        Tournament testTournament = new Tournament();
        testTournament.setTournament_name("test");
        testTournament.setId(tId);
        testTournament.setStatus("active");
        testTournament.setNoOfRounds(roundNo);
        List<Round> rounds = new ArrayList<Round>(List.of(new Round()));
        testTournament.setRounds(rounds);

        when(tournamentRepository.findById(tId)).thenReturn(Optional.of(testTournament));
        when(tournamentRepository.save(testTournament)).thenReturn(testTournament);
        when(roundService.createNextRound(tId)).thenReturn(new Round());

        tournamentService.checkComplete(tId);

        assertEquals(2, testTournament.getRounds().size());
        verify(tournamentRepository).findById(tId);
        verify(tournamentRepository).save(testTournament);
    }

    @Test
    void checkComplete_Failure_ReturnException() throws Exception {
        Long tId = (long) 11;

        when(tournamentRepository.findById(tId)).thenReturn(Optional.empty());
        
        Exception exception = assertThrows(Exception.class, () -> {
            tournamentService.checkComplete(tId);
        });
        assertEquals("Tournament not found", exception.getMessage());

        verify(tournamentRepository).findById(tId);
    }

    @Test
    void endTournament_Success_ReturnUpdatedTournament() throws Exception {
        Long tId = (long) 11;
        Tournament testTournament = new Tournament();
        testTournament.setTournament_name("test");
        testTournament.setId(tId);
        testTournament.setStatus("active");

        when(tournamentRepository.findById(tId)).thenReturn(Optional.of(testTournament));
        when(tournamentRepository.save(testTournament)).thenReturn(testTournament);

        tournamentService.endTournament(tId);

        assertEquals("completed", testTournament.getStatus());
        verify(tournamentRepository).findById(tId);
        verify(tournamentRepository).save(testTournament);
    }

    @Test
    void endTournament_Failure_ReturnException() throws Exception {
        Long tId = (long) 11;

        when(tournamentRepository.findById(tId)).thenReturn(Optional.empty());


        Exception exception = assertThrows(Exception.class, () -> {
            tournamentService.endTournament(tId);
        });
        assertEquals("Tournament not found", exception.getMessage());

        verify(tournamentRepository).findById(tId);
    }

    @Test
    void deleteTournament_Success_ReturnVoid() throws Exception{
        Long tId = (long) 11;
        Tournament testTournament = new Tournament();
        testTournament.setTournament_name("test");
        testTournament.setId(tId);
        testTournament.setStatus("active");

        when(tournamentRepository.findById(tId)).thenReturn(Optional.of(testTournament));
        when(tournamentRepository.save(testTournament)).thenReturn(testTournament);
        
        tournamentService.deleteTournament(tId);

        verify(tournamentRepository,times(2)).findById(tId);
        verify(tournamentRepository).save(testTournament);
        verify(tournamentRepository).deleteById(tId);
    }

    @Test
    void deleteTournament_Failure_TournamentNotFound() throws Exception{
        Long tId = (long) 11;
        when(tournamentRepository.findById(tId)).thenReturn(Optional.empty());
        
        Exception exception = assertThrows(Exception.class, () -> {
            tournamentService.deleteTournament(tId);
        });
        assertEquals("Tournament not found", exception.getMessage());

        verify(tournamentRepository).findById(tId);
    }

    @Test
    void removeAllUsers_Success_UsersRemovedFromTournament() throws Exception{
        Long tId = (long) 11;
        Tournament testTournament = new Tournament();
        testTournament.setTournament_name("test");
        testTournament.setId(tId);
        testTournament.setStatus("active");

        User testUser=new User();
        testUser.setId(tId);
        testUser.setCurrentTournaments(new ArrayList<>(List.of(testTournament)));
        testTournament.setParticipants(new ArrayList<>(List.of(testUser)));

        when(tournamentRepository.findById(tId)).thenReturn(Optional.of(testTournament));
        when(tournamentRepository.save(testTournament)).thenReturn(testTournament);

        tournamentService.removeAllUsers(tId);

        assertEquals(0,testTournament.getParticipants().size());
        assertEquals(0,testUser.getCurrentTournaments().size());
        verify(tournamentRepository).findById(tId);
        verify(tournamentRepository).save(testTournament);
    }

    @Test
    void removeAllUsers_Failure_TournamentNotFound() throws Exception{
        Long tId = (long) 11;

        when(tournamentRepository.findById(tId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            tournamentService.removeAllUsers(tId);
        });
        assertEquals("Tournament not found", exception.getMessage());

        verify(tournamentRepository).findById(tId);
    }

}
