package com.codewithcled.fullstack_backend_proj1;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.codewithcled.fullstack_backend_proj1.DTO.SignInRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.SignUpRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.UserDTO;
import com.codewithcled.fullstack_backend_proj1.DTO.UserMapper;
import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;
import com.codewithcled.fullstack_backend_proj1.response.AuthResponse;
import com.codewithcled.fullstack_backend_proj1.service.UserServiceImplementation;

import org.junit.jupiter.api.BeforeEach;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImplementation userService;

    @Test
    void loadUserByUsername(String username) {
        String userName = "testUser";
        User testUser = new User();
        testUser.setEmail(userName);
        testUser.setId((long) 11);

        when(userRepository.findByEmail(userName)).thenReturn(testUser);
        when(passwordEncoder.matches(username, userName)).thenReturn(true);
        try {
            UserDetails result = userService.loadUserByUsername(userName);

            assertEquals(userName, result.getUsername());
            verify(userRepository,times(2)).findByEmail(userName);
        } catch (UsernameNotFoundException e) {

        }
    }

    @Test
    void getAllUser() {
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

    /*
     * void findUserProfileByJwt(){
     * String username = "test";
     * Long id = (long) 10;
     * User testUser = new User();
     * String JWT="";
     * testUser.setUsername(username);
     * testUser.setId(id);
     * 
     * when(userRepository.findByJWT(JWT)).thenReturn(testUser);
     * 
     * User result=userService.findUserProfileByJWT(JWT);
     * 
     * assertEquals(testUser,result);
     * verify(userRepository).findByJWT(JWT);
     * };
     */

    @Test
    void findUserByEmail() {
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

    /*
     * void findUserById(String userId){
     * String username = "test";
     * Long id = (long) 10;
     * User testUser = new User();
     * testUser.setUsername(username);
     * testUser.setId(id);
     * 
     * when(userRepository.findById(id)).thenReturn(testUser);
     * 
     * User result=userService.findUserById(id);
     * 
     * assertEquals(testUser, result);
     * verify(userRepository).findById(id);
     * }
     */

    @Test
    void findAllUsers() {
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
    void findAllUsersDTO() {
        String username = "test";
        String role = "ROLE_USER";
        Long id = (long) 10;
        User testUser = new User();
        testUser.setUsername(username);
        testUser.setId(id);
        testUser.setRole(role);

        List<User> testList = new ArrayList<User>();
        testList.add(testUser);

        List<UserDTO> DTOList = UserMapper.toDTOList(testList);

        when(userRepository.findByRole("ROLE_USER")).thenReturn(testList);

        List<UserDTO> result = userService.findAllUsersDTO();

        assertEquals(DTOList.get(0).getUsername(), result.get(0).getUsername());
        verify(userRepository).findByRole("ROLE_USER");
    }

    @Test
    void loadByUsername() {
        String userName = "testUser";
        Long uId = (long) 10;
        User testUser = new User();
        testUser.setEmail(userName);
        testUser.setId(uId);

        when(userRepository.findByEmail(userName)).thenReturn(testUser);

        try {
            User result = userService.loadByUsername(userName);

            assertEquals(testUser, result);
            verify(userRepository).findByEmail(userName);

        } catch (Exception e) {

        }
    }

    @Test
    void createUser(SignUpRequest user) {
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

        User testUser = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setRole(role);
        user.setElo(elo);

        when(userRepository.findByEmail(email)).thenReturn(null);
        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(passwordEncoder.matches(password, password)).thenReturn(true);
        try {

            AuthResponse result = userService.createUser(signUpRequestDetails);

            assertEquals("Register Success", result.getMessage());
            verify(userRepository,times(2)).findByEmail(email);
            verify(userRepository).existsByUsername(username);
            verify(userRepository).save(testUser);

        } catch (Exception e) {

        }
    }

    @Test
    void signInUser() {
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
        verify(userRepository,times(2)).findByEmail(username);
    }

    @Test
    void updateUser(Long id, SignUpRequest newUser) {
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

        Optional<User> returnUser = Optional.of(oldUser);

        when(userRepository.findById(uId)).thenReturn(returnUser);
        when(passwordEncoder.matches(password, password)).thenReturn(true);

        Optional<User> result = userService.updateUser(uId, newUser);

        assertEquals(true, result.isPresent());
        assertEquals(newUsername, result.get().getUsername());

        verify(userRepository,times(2)).findById(uId);
    }

    @Test
    void getUserParticipatingTournaments(Long userId) {
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

        Optional<User> returnUser = Optional.of(testUser);
        when(userRepository.findById(uId)).thenReturn(returnUser);
        when(passwordEncoder.matches("", "")).thenReturn(true);

        try {
            List<Tournament> result = userService.getUserParticipatingTournaments(userId);

            assertIterableEquals(tournamentList, result);
            verify(userRepository,times(2)).findById(uId);
        } catch (Exception e) {

        }
    }
}