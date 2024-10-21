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
    void getTournamentParticipants_failure_returnError() {
        Long id = (long) 500;
        boolean exceptionThrown=false;

        when(tournamentRepository.findById(id)).thenReturn(Optional.empty());

        try {
            tournamentService.getTournamentParticipants(id);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Error Occured");
            verify(tournamentRepository).findById(id);

            exceptionThrown=true;
        }

        assertTrue(exceptionThrown);
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
        List<Tournament> finalTournamentList=new ArrayList<Tournament>();
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
        boolean exceptionThrown=false;

        when(tournamentRepository.findById(tId)).thenReturn(returnTournament);

        try {

            tournamentService.updateUserParticipating(uIdT, tId);

        } catch (Exception e) {
            assertEquals("Tournament not found", e.getMessage());
            verify(tournamentRepository).findById(tId);
            verify(userRepository, never()).findById(uIdT);

            exceptionThrown=true;
        }

        assertTrue(exceptionThrown);
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
        boolean exceptionThrown=false;

        when(tournamentRepository.findById(tId)).thenReturn(returnTournament);
        when(userRepository.findById(uIdT)).thenReturn(returnUser);
        try {

            tournamentService.updateUserParticipating(uIdT, tId);

        } catch (Exception e) {
            assertEquals("User not found", e.getMessage());
            verify(tournamentRepository).findById(tId);
            verify(userRepository).findById(uIdT);

            exceptionThrown=true;
        }

        assertTrue(exceptionThrown);
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
        List<Tournament> finalTournamentList=new ArrayList<Tournament>();

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
    void removeUserParticipating_Failure_TournamentNotFound_ReturnError() {
        Long uId = (long) 10;
        Long tId = (long) 11;
        Tournament testTournament = new Tournament();
        User firstUser = new User();
        firstUser.setUsername("FirstUser");
        testTournament.addParticipant(new User());
        Optional<Tournament> returnTournament = Optional.empty();
        boolean exceptionThrown=false;

        when(tournamentRepository.findById(tId)).thenReturn(returnTournament);

        
        try {

            tournamentService.removeUserParticipating(uId, tId);

        } catch (Exception e) {
            assertEquals("Tournament not found", e.getMessage());
            verify(tournamentRepository).findById(tId);

            exceptionThrown=true;
        }

        assertTrue(exceptionThrown);
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
        boolean exceptionThrown=false;

        when(tournamentRepository.findById(tId)).thenReturn(returnTournament);
        when(userRepository.findById(uId)).thenReturn(returnUser);
        try {

            tournamentService.removeUserParticipating(uId, tId);

        } catch (Exception e) {
            assertEquals("User not found", e.getMessage());
            verify(tournamentRepository).findById(tId);
            verify(userRepository).findById(uId);

            exceptionThrown=true;
        }

        assertTrue(exceptionThrown);
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
        boolean exceptionThrown=false;

        when(tournamentRepository.findById(tId)).thenReturn(returnTournament);
        when(userRepository.findById(uId)).thenReturn(returnUser);
        try {

            tournamentService.removeUserParticipating(uId, tId);

        } catch (Exception e) {
            assertEquals("User is not participating in the tournament", e.getMessage());
            verify(tournamentRepository).findById(tId);
            verify(userRepository).findById(uId);

            exceptionThrown=true;
        }

        assertTrue(exceptionThrown);
    }

    @Test
    void updateTournament_Success_ReturnTournament() throws Exception {
        Long tId = (long) 11;
        CreateTournamentRequest tournamentUpdateData = new CreateTournamentRequest();
        tournamentUpdateData.setTournament_name("newName");
        Tournament originalTournament = new Tournament();
        originalTournament.setTournament_name("oldName");
        Optional<Tournament> returnTournament = Optional.of(originalTournament);

        when(tournamentRepository.findById(tId)).thenReturn(returnTournament);
        when(tournamentRepository.save(originalTournament)).thenReturn(originalTournament);

        Tournament result = tournamentService.updateTournament(tId, tournamentUpdateData);

        assertEquals(tournamentUpdateData.getTournament_name(), result.getTournament_name());
        verify(tournamentRepository).findById(tId);
        verify(tournamentRepository).save(originalTournament);

    }

    @Test
    void updateTournament_Failure_TournamentNotFound_ReturnError() {
        Long tId = (long) 11;
        CreateTournamentRequest tournamentUpdateData = new CreateTournamentRequest();
        tournamentUpdateData.setTournament_name("newName");
        Tournament originalTournament = new Tournament();
        originalTournament.setTournament_name("oldName");
        boolean exceptionThrown=false;

        when(tournamentRepository.findById(tId)).thenReturn(Optional.empty());

        try {

            tournamentService.updateTournament(tId, tournamentUpdateData);

        } catch (Exception e) {
            assertEquals("Tournament not found", e.getMessage());
            verify(tournamentRepository).findById(tId);

            exceptionThrown=true;
        }

        assertTrue(exceptionThrown);
    }

    @Test
    void getTournamentsWithNoCurrentUser_Success_ReturnTournamentList() throws Exception {
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
        when(userRepository.findById(uId)).thenReturn(Optional.of(testUser));

        List<Tournament> result = tournamentService.getTournamentsWithNoCurrentUser(uId);

        assertIterableEquals(resultList, result);
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
        Optional<User> returnUser = Optional.empty();
        boolean exceptionThrown=false;

        when(tournamentRepository.findAll()).thenReturn(resultList);
        when(userRepository.findById(uId)).thenReturn(returnUser);

        try {
            tournamentService.getTournamentsWithNoCurrentUser(uId);

        } catch (Exception e) {
            assertEquals("User not found", e.getMessage());
            verify(tournamentRepository).findAll();
            verify(userRepository).findById(uId);
            exceptionThrown=true;
        }

        assertTrue(exceptionThrown);

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
    void getNonParticipatingCurrentUser_Success_ReturnUserList() throws Exception {
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

        List<User> result = tournamentService.getNonParticipatingCurrentUser(tId);

        assertEquals(1, result.size());
        assertEquals(tUser2, result.get(0));
        verify(tournamentRepository).findById(tId);
        verify(userRepository).findAll();
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

        boolean exceptionThrown=false;

        try {

            tournamentService.getNonParticipatingCurrentUser(tId);

        } catch (Exception e) {

            assertEquals("Tournament not found", e.getMessage());
            verify(tournamentRepository).findById(tId);
            verify(userRepository, never()).findAll();
            exceptionThrown=true;

        }
        assertTrue(exceptionThrown);
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
    void getActiveTournament_Success(){
        Tournament testTournament=new Tournament();
        testTournament.setStatus("active");
        Tournament testTournament2=new Tournament();
        testTournament2.setStatus("ongoing");
        List<Tournament> tournamentList=new ArrayList<Tournament>(List.of(testTournament,testTournament2));
        
        when(tournamentRepository.findAll()).thenReturn(tournamentList);

        List<Tournament> result=tournamentService.getActiveTournament();

        assertEquals(1,result.size());
        assertEquals("active",result.get(0).getStatus());
        verify(tournamentRepository).findAll();
    }

    @Test
    void getInActiveTournament_Success(){
        Tournament testTournament=new Tournament();
        testTournament.setStatus("active");
        Tournament testTournament2=new Tournament();
        testTournament2.setStatus("ongoing");
        List<Tournament> tournamentList=new ArrayList<Tournament>(List.of(testTournament,testTournament2));
        
        when(tournamentRepository.findAll()).thenReturn(tournamentList);

        List<Tournament> result=tournamentService.getInactiveTournament();

        assertEquals(1,result.size());
        assertEquals("ongoing",result.get(0).getStatus());
        verify(tournamentRepository).findAll();
    }

    @Test
    void startTournament_Success() throws Exception{
        Long tId = (long) 11;
        int roundNo=2;
        Tournament testTournament = new Tournament();
        testTournament.setTournament_name("test");
        testTournament.setId(tId);
        testTournament.setStatus("active");
        testTournament.setNoOfRounds(roundNo);
        List<Round> rounds=new ArrayList<Round>();
        testTournament.setRounds(rounds);

        Round testRound=new Round();

        when(tournamentRepository.findById(tId)).thenReturn(Optional.of(testTournament));
        when(roundService.createFirstRound(tId)).thenReturn(testRound);
        when(tournamentRepository.save(testTournament)).thenReturn(testTournament);

        Tournament result=tournamentService.startTournament(tId);

        assertEquals(1,result.getRounds().size());
        assertEquals("ongoing",result.getStatus());
        verify(tournamentRepository).findById(tId);
        verify(roundService).createFirstRound(tId);
        verify(tournamentRepository).save(testTournament);
    }

    @Test
    void startTournament_Failure_TournamentNotActive() throws Exception{
        Long tId = (long) 11;
        int roundNo=2;
        Tournament testTournament = new Tournament();
        testTournament.setTournament_name("test");
        testTournament.setId(tId);
        testTournament.setStatus("ongoing");
        testTournament.setNoOfRounds(roundNo);
        List<Round> rounds=new ArrayList<Round>();
        testTournament.setRounds(rounds);


        when(tournamentRepository.findById(tId)).thenReturn(Optional.of(testTournament));

        boolean exceptionThrown=false;
        try {
            tournamentService.startTournament(tId);
        } catch (Exception e) {
            assertEquals("Tournament is ongoing or completed",e.getMessage());
            exceptionThrown=true;
        }
        

        assertTrue(exceptionThrown);

        verify(tournamentRepository).findById(tId);
    }

    @Test
    void startTournament_Failure_TournamentNotFound() throws Exception{
        Long tId = (long) 11;
        when(tournamentRepository.findById(tId)).thenReturn(Optional.empty());

        boolean exceptionThrown=false;
        try {
            tournamentService.startTournament(tId);
        } catch (Exception e) {
            assertEquals("Tournament not found",e.getMessage());
            exceptionThrown=true;
        }
        

        assertTrue(exceptionThrown);

        verify(tournamentRepository).findById(tId);
    }

    @Test
    void checkComplete_Success_RoundsEqualNoOfRounds_EndTournament() throws Exception{
        Long tId = (long) 11;
        int roundNo=1;
        Tournament testTournament = new Tournament();
        testTournament.setTournament_name("test");
        testTournament.setId(tId);
        testTournament.setStatus("active");
        testTournament.setNoOfRounds(roundNo);
        List<Round> rounds=new ArrayList<Round>(List.of(new Round()));
        testTournament.setRounds(rounds);

        when(tournamentRepository.findById(tId)).thenReturn(Optional.of(testTournament));
        when(tournamentRepository.save(testTournament)).thenReturn(testTournament);

        tournamentService.checkComplete(tId);


        assertEquals("completed",testTournament.getStatus());
        verify(tournamentRepository,times(2)).findById(tId);
        verify(tournamentRepository).save(testTournament);
    }

    @Test
    void checkComplete_Success_RoundsNotEqualNoOfRounds_AddNewRound() throws Exception{
        Long tId = (long) 11;
        int roundNo=4;
        Tournament testTournament = new Tournament();
        testTournament.setTournament_name("test");
        testTournament.setId(tId);
        testTournament.setStatus("active");
        testTournament.setNoOfRounds(roundNo);
        List<Round> rounds=new ArrayList<Round>(List.of(new Round()));
        testTournament.setRounds(rounds);

        when(tournamentRepository.findById(tId)).thenReturn(Optional.of(testTournament));
        when(tournamentRepository.save(testTournament)).thenReturn(testTournament);
        when(roundService.createNextRound(tId)).thenReturn(new Round());

        tournamentService.checkComplete(tId);


        assertEquals(2,testTournament.getRounds().size());
        verify(tournamentRepository).findById(tId);
        verify(tournamentRepository).save(testTournament);
    }

    @Test
    void checkComplete_Failure() throws Exception{
        Long tId = (long) 11;

        when(tournamentRepository.findById(tId)).thenReturn(Optional.empty());
        boolean exceptionThrown=false;
        try {
            tournamentService.checkComplete(tId);
        } catch (Exception e) {
            assertEquals("Tournament not found",e.getMessage());
            exceptionThrown=true;
        }
        


        assertTrue(exceptionThrown);
        verify(tournamentRepository).findById(tId);
    }

    @Test
    void endTournament_Success() throws Exception{
        Long tId = (long) 11;
        Tournament testTournament = new Tournament();
        testTournament.setTournament_name("test");
        testTournament.setId(tId);
        testTournament.setStatus("active");

        when(tournamentRepository.findById(tId)).thenReturn(Optional.of(testTournament));
        when(tournamentRepository.save(testTournament)).thenReturn(testTournament);

        tournamentService.endTournament(tId);

        assertEquals("completed",testTournament.getStatus());
        verify(tournamentRepository).findById(tId);
        verify(tournamentRepository).save(testTournament);
    }

    @Test
    void endTournament_Failure() throws Exception{
        Long tId = (long) 11;

        when(tournamentRepository.findById(tId)).thenReturn(Optional.empty());

        boolean exceptionThrown=false;

        try {
            tournamentService.endTournament(tId);
        } catch (Exception e) {
            assertEquals("Tournament not found",e.getMessage());
            exceptionThrown=true;
        }
        

        assertTrue(exceptionThrown);
        verify(tournamentRepository).findById(tId);
    }

}