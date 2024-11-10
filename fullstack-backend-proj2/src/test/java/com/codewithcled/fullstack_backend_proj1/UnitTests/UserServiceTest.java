package com.codewithcled.fullstack_backend_proj1.UnitTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.codewithcled.fullstack_backend_proj1.DTO.EditUserRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.SignInRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.SignUpRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.UserDTO;
import com.codewithcled.fullstack_backend_proj1.DTO.UserMapper;
import com.codewithcled.fullstack_backend_proj1.model.Match;
import com.codewithcled.fullstack_backend_proj1.model.Round;
import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.repository.MatchRepository;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;
import com.codewithcled.fullstack_backend_proj1.response.AuthResponse;
import com.codewithcled.fullstack_backend_proj1.service.UserServiceImplementation;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImplementation userService;

    @Test
    void loadUserByUsername_Success_ReturnUserDetails() throws Exception {
        String userName = "testUser";
        User testUser = new User();
        testUser.setPassword(userName);
        testUser.setEmail(userName);
        testUser.setId((long) 11);

        when(userRepository.findByEmail(userName)).thenReturn(testUser);

        UserDetails result = userService.loadUserByUsername(userName);

        assertEquals(userName, result.getUsername());
        verify(userRepository).findByEmail(userName);
    }

    @Test
    void loadUserByUsername_Failure_ReturnException() throws Exception {
        String userName = "testUser";
        User testUser = new User();
        testUser.setPassword(userName);
        testUser.setEmail(userName);
        testUser.setId((long) 11);

        when(userRepository.findByEmail(userName)).thenReturn(null);

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername(userName);
        });
        assertEquals("User not found with this email" + userName, exception.getMessage());

        verify(userRepository).findByEmail(userName);
    }

    @Test
    void getAllUser_Success_returnUserList() {
        String username = "test";
        Long id = (long) 10;
        User testUser = new User();
        testUser.setUsername(username);
        testUser.setId(id);
        List<User> testUserList = new ArrayList<User>();
        testUserList.add(testUser);

        when(userRepository.findAll()).thenReturn(testUserList);

        List<User> result = userService.getAllUser();

        assertIterableEquals(testUserList, result);
        verify(userRepository).findAll();
    }

    @Test
    void findUserByEmail_Success_ReturnUser() {
        String username = "test";
        Long id = (long) 10;
        User testUser = new User();
        testUser.setUsername(username);
        testUser.setId(id);
        testUser.setEmail(username);

        when(userRepository.findByEmail(username)).thenReturn(testUser);

        User result = userService.findUserByEmail(username);

        assertEquals(testUser, result);
        verify(userRepository).findByEmail(username);
    }

    @Test
    void findAllUsers_Success_ReturnUserList() {
        String username = "test";
        Long id = (long) 10;
        User testUser = new User();
        testUser.setUsername(username);
        testUser.setId(id);
        List<User> testUserList = new ArrayList<User>();
        testUserList.add(testUser);

        when(userRepository.findAll()).thenReturn(testUserList);

        List<User> result = userService.findAllUsers();

        assertIterableEquals(testUserList, result);
        verify(userRepository).findAll();
    }

    @Test
    void findAllUsersDTO_Success_ReturnUserList() {
        String username = "test";
        String role = "ROLE_USER";
        Long id = (long) 10;
        User testUser = new User();
        testUser.setUsername(username);
        testUser.setId(id);
        testUser.setRole(role);
        testUser.setElo(1000.0);

        User testUser2 = new User();
        testUser2.setUsername(username+2);
        testUser2.setId(id+2);
        testUser2.setRole(role);
        testUser2.setElo(2000.0);

        List<User> testList = new ArrayList<User>();
        testList.add(testUser);
        testList.add(testUser2);

        List<UserDTO> DTOList = UserMapper.toDTOList(testList);

        when(userRepository.findByRole("ROLE_USER")).thenReturn(testList);

        List<UserDTO> result = userService.findAllUsersDTO();

        assertEquals(2,result.size());
        assertEquals(DTOList.get(1).getUsername(), result.get(0).getUsername());
        verify(userRepository).findByRole("ROLE_USER");
    }

    @Test
    void loadByUsername_Success_ReturnUser() throws Exception {
        String userName = "testUser";
        Long uId = (long) 10;
        User testUser = new User();
        testUser.setEmail(userName);
        testUser.setId(uId);

        when(userRepository.findByEmail(userName)).thenReturn(testUser);

        User result = userService.loadByUsername(userName);

        assertEquals(testUser, result);
        verify(userRepository).findByEmail(userName);

    }

    @Test
    void createUser_Success_ReturnAuthResponse() throws Exception {
        String username = "test";
        String password = "password";
        String email = "email@test.com";
        String role = "ROLE_USER";

        SignUpRequest signUpRequestDetails = new SignUpRequest();
        signUpRequestDetails.setUsername(username);
        signUpRequestDetails.setPassword(password);
        signUpRequestDetails.setEmail(email);
        signUpRequestDetails.setRole(role);

        when(userRepository.findByEmail(email)).thenReturn(null);
        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.save(any())).thenAnswer(invocation -> {
            return invocation.getArgument(0);
        });
        when(passwordEncoder.encode(password)).thenReturn(password);

        AuthResponse result = userService.createUser(signUpRequestDetails);

        assertEquals("Register Success", result.getMessage());
        verify(userRepository).findByEmail(email);
        verify(userRepository).existsByUsername(username);
        verify(userRepository).save(any());
        verify(passwordEncoder).encode(password);

    }

    @Test
    void createUser_Success_createAdmin_ReturnAuthResponse() throws Exception {
        String username = "test";
        String password = "password";
        String email = "email@test.com";
        String role = "ROLE_ADMIN";

        SignUpRequest signUpRequestDetails = new SignUpRequest();
        signUpRequestDetails.setUsername(username);
        signUpRequestDetails.setPassword(password);
        signUpRequestDetails.setEmail(email);
        signUpRequestDetails.setRole(role);

        when(userRepository.findByEmail(email)).thenReturn(null);
        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.save(any())).thenAnswer(invocation -> {
            return invocation.getArgument(0);
        });
        when(passwordEncoder.encode(password)).thenReturn(password);

        AuthResponse result = userService.createUser(signUpRequestDetails);

        assertEquals("Register Success", result.getMessage());
        verify(userRepository).findByEmail(email);
        verify(userRepository).existsByUsername(username);
        verify(userRepository).save(any());
        verify(passwordEncoder).encode(password);

    }

    @Test
    void createUser_Failure_InValidEmailFormat_ReturnException() throws Exception {
        String username = "test";
        String password = "password";
        String email = "email";
        String role = "ROLE_USER";
        double elo = 100;

        SignUpRequest signUpRequestDetails = new SignUpRequest();
        signUpRequestDetails.setUsername(username);
        signUpRequestDetails.setPassword(password);
        signUpRequestDetails.setEmail(email);
        signUpRequestDetails.setRole(role);
        signUpRequestDetails.setElo(elo);

        Exception exception = assertThrows(Exception.class, () -> {
            userService.createUser(signUpRequestDetails);
        });
        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    void createUser_Failure_SameEmail_ReturnException() throws Exception {
        String username = "test";
        String password = "password";
        String email = "email@test.com";
        String role = "ROLE_USER";
        double elo = 100;

        SignUpRequest signUpRequestDetails = new SignUpRequest();
        signUpRequestDetails.setUsername(username);
        signUpRequestDetails.setPassword(password);
        signUpRequestDetails.setEmail(email);
        signUpRequestDetails.setRole(role);
        signUpRequestDetails.setElo(elo);

        User testUser = new User();
        testUser.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(testUser);

        Exception exception = assertThrows(Exception.class, () -> {
            userService.createUser(signUpRequestDetails);
        });
        assertEquals("Email is already used with another account", exception.getMessage());

        verify(userRepository).findByEmail(email);
    }

    @Test
    void createUser_Failure_SameUserName_ReturnException() throws Exception {
        String username = "test";
        String password = "password";
        String email = "email@test.com";
        String role = "ROLE_USER";
        double elo = 100;

        SignUpRequest signUpRequestDetails = new SignUpRequest();
        signUpRequestDetails.setUsername(username);
        signUpRequestDetails.setPassword(password);
        signUpRequestDetails.setEmail(email);
        signUpRequestDetails.setRole(role);
        signUpRequestDetails.setElo(elo);

        when(userRepository.findByEmail(email)).thenReturn(null);
        when(userRepository.existsByUsername(username)).thenReturn(true);

        Exception exception = assertThrows(Exception.class, () -> {
            userService.createUser(signUpRequestDetails);
        });
        assertEquals("Username is already used with another account", exception.getMessage());

        verify(userRepository).findByEmail(email);
        verify(userRepository).existsByUsername(username);
    }

    @Test
    void signInUser_Success_ReturnAuthResponse() {
        SignInRequest loginRequest = new SignInRequest();
        String username = "testUser";
        String password = "password";
        String role = "ROLE_USER";

        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        User testUser = new User();
        testUser.setUsername(username);
        testUser.setPassword(password);
        testUser.setRole(role);
        testUser.setEmail(username);
        testUser.setId((long) 11);

        when(userRepository.findByEmail(username)).thenReturn(testUser);
        when(passwordEncoder.matches(password, password)).thenReturn(true);

        AuthResponse result = userService.signInUser(loginRequest);

        assertEquals("Login success", result.getMessage());
        verify(userRepository, times(2)).findByEmail(username);
    }

    @Test
    void updateUser_Success_ReturnOptionalUser() {
        Long uId = (long) 11;
        SignUpRequest updateUserDetails = new SignUpRequest();

        String username = "testUser";
        String password = "password";
        String role = "ROLE_USER";
        int elo = 1000;

        User oldUser = new User();
        oldUser.setUsername(username);
        oldUser.setEmail(username);
        oldUser.setPassword(password);
        oldUser.setRole(role);
        oldUser.setElo((double) elo);

        String newUsername = "newUser";
        updateUserDetails.setUsername(newUsername);
        updateUserDetails.setEmail(username);
        updateUserDetails.setPassword(password);
        updateUserDetails.setRole(role);
        updateUserDetails.setElo((double) elo);

        when(userRepository.findById(uId)).thenReturn(Optional.of(oldUser));
        when(passwordEncoder.encode(password)).thenReturn(password);
        when(userRepository.save(oldUser)).thenReturn(oldUser);

        Optional<User> result = userService.updateUser(uId, updateUserDetails);

        assertEquals(true, result.isPresent());
        assertEquals(newUsername, result.get().getUsername());

        verify(userRepository).findById(uId);
        verify(passwordEncoder).encode(password);
        verify(userRepository).save(oldUser);
    }

    @Test
    void updateUser_Failure_returnOptionalEmpty() {
        Long uId = (long) 11;
        SignUpRequest updateUserDetails = new SignUpRequest();

        String username = "testUser";
        String password = "password";
        String role = "ROLE_USER";
        int elo = 1000;

        String newUsername = "newUser";
        updateUserDetails.setUsername(newUsername);
        updateUserDetails.setEmail(username);
        updateUserDetails.setPassword(password);
        updateUserDetails.setRole(role);
        updateUserDetails.setElo((double) elo);

        Optional<User> returnUser = Optional.empty();

        when(userRepository.findById(uId)).thenReturn(returnUser);

        Optional<User> result = userService.updateUser(uId, updateUserDetails);

        assertEquals(false, result.isPresent());

        verify(userRepository).findById(uId);
    }

    @Test
    void getUserParticipatingTournaments_Success_ReturnUserTournamentList() throws Exception {
        Long uId = (long) 11;

        List<Tournament> tournamentList = new ArrayList<Tournament>();
        Tournament testTournament = new Tournament();
        testTournament.setTournament_name("TestT");
        testTournament.setId((long) 12);
        tournamentList.add(testTournament);

        User testUser = new User();
        testUser.setId(uId);
        testUser.setUsername("testUser");
        testUser.setCurrentTournaments(tournamentList);

        when(userRepository.findById(uId)).thenReturn(Optional.of(testUser));

        List<Tournament> result = userService.getUserParticipatingTournaments(uId);

        assertIterableEquals(tournamentList, result);
        verify(userRepository).findById(uId);
    }

    @Test
    void getUserParticipatingTournaments_Failure_ReturnException() throws Exception {
        Long uId = (long) 11;

        List<Tournament> tournamentList = new ArrayList<Tournament>();
        Tournament testTournament = new Tournament();
        testTournament.setTournament_name("TestT");
        testTournament.setId((long) 12);
        tournamentList.add(testTournament);

        User testUser = new User();
        testUser.setId(uId);
        testUser.setUsername("testUser");
        testUser.setCurrentTournaments(tournamentList);

        when(userRepository.findById(uId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            userService.getUserParticipatingTournaments(uId);
        });
        assertEquals("User not found", exception.getMessage());

        verify(userRepository).findById(uId);
    }

    @Test
    public void getUserPastMatches_Success_ReturnListOfMatches() throws Exception {
        Long uId = (long) 1234;
        User testUser = new User();
        testUser.setId(uId);
        testUser.setUsername("testUser");
        testUser.setEmail("testUser");

        List<User> userList = List.of(testUser);

        Tournament testTournament = new Tournament();
        testTournament.setSize(6);
        testTournament.setCurrentSize(2);
        testTournament.setTournament_name("tournamentName");

        List<Tournament> tournametList = List.of(testTournament);
        testTournament.setParticipants(userList);
        testUser.setCurrentTournaments(tournametList);

        Round testRound = new Round();
        testRound.setTournament(testTournament);

        Match testMatch = new Match();
        testMatch.setIsComplete(true);
        testMatch.setPlayer1(uId);
        testMatch.setPlayer2(uId);
        testMatch.setRound(testRound);
        List<Match> returnList = List.of(testMatch);

        when(userRepository.findById(uId)).thenReturn(Optional.of(testUser));
        when(matchRepository.findByIsCompleteAndPlayer1OrIsCompleteAndPlayer2(true, testUser.getId(), true, testUser.getId()))
                .thenReturn(returnList);

        List<Match> result = userService.getUserPastMatches(uId);

        assertIterableEquals(returnList, result);

        verify(userRepository).findById(uId);
        verify(matchRepository).findByIsCompleteAndPlayer1OrIsCompleteAndPlayer2(true, testUser.getId(), true, testUser.getId());
    }

    @Test
    public void getUserPastMatches_Failure_UserNotFound_ReturnException() throws Exception {
        Long uId = (long) 1234;
        User testUser = new User();
        testUser.setId(uId);
        testUser.setUsername("testUser");
        testUser.setEmail("testUser");

        List<User> userList = List.of(testUser);

        Tournament testTournament = new Tournament();
        testTournament.setSize(6);
        testTournament.setCurrentSize(2);
        testTournament.setTournament_name("tournamentName");

        List<Tournament> tournametList = List.of(testTournament);
        testTournament.setParticipants(userList);
        testUser.setCurrentTournaments(tournametList);

        Round testRound = new Round();
        testRound.setTournament(testTournament);

        Match testMatch = new Match();
        testMatch.setIsComplete(true);
        testMatch.setPlayer1(uId);
        testMatch.setPlayer2(uId);
        testMatch.setRound(testRound);

        when(userRepository.findById(uId)).thenReturn(Optional.empty());

        
        Exception exception = assertThrows(Exception.class, () -> {
            userService.getUserPastMatches(uId);
        });
        assertEquals("User not found", exception.getMessage());

        verify(userRepository).findById(uId);
    }

    @Test
    void updateUserWithoutPassword_Success_ReturnOptionalUser() {
        Long uId = (long) 123;
        User testUser = new User();
        testUser.setId(uId);
        testUser.setUsername("oldUsername");
        testUser.setElo((double) 1200);
        testUser.setRole("ROLE_USER");

        EditUserRequest editUserRequest = new EditUserRequest();
        editUserRequest.setUsername("newUsername");
        editUserRequest.setElo((double) 1500);
        editUserRequest.setRole("ROLE_ADMIN");
        when(userRepository.findById(uId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);

        Optional<User> updatedUser = userService.updateUserWithoutPassword(uId, editUserRequest);

        assertTrue(updatedUser.isPresent());
        assertEquals("newUsername", updatedUser.get().getUsername());
        assertEquals(1500, updatedUser.get().getElo());
        assertEquals("ROLE_ADMIN", updatedUser.get().getRole());
        verify(userRepository).findById(uId);
        verify(userRepository).save(testUser);
    }

    @Test
    void updateUserWithoutPassword_Failure_UserNotFound_ReturnOptionalEmpty() {
        Long uId = (long) 123;

        EditUserRequest editUserRequest = new EditUserRequest();
        editUserRequest.setUsername("newUsername");
        editUserRequest.setElo((double) 1500);
        editUserRequest.setRole("ROLE_ADMIN");

        when(userRepository.findById(uId)).thenReturn(Optional.empty());

        Optional<User> updatedUser = userService.updateUserWithoutPassword(uId, editUserRequest);

        assertFalse(updatedUser.isPresent());
        verify(userRepository, never()).save(any(User.class)); // Ensure save was not called
    }
}
