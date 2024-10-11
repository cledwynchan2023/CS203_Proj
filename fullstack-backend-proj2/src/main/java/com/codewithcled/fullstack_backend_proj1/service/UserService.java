package com.codewithcled.fullstack_backend_proj1.service;

import com.codewithcled.fullstack_backend_proj1.DTO.EditUserRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.SignInRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.SignUpRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.UserDTO;
import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.response.AuthResponse;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;


public interface UserService {


    public List<User> getAllUser()  ;

    public User findUserProfileByJwt(String jwt);

    public User findUserByEmail(String email) ;

    public User findUserById(String userId) ;

    public List<User> findAllUsers();
    public List<UserDTO> findAllUsersDTO();
    // public List<UserDTO> getUserChanges();
    public User loadByUsername(String username);

    public AuthResponse createUser(SignUpRequest user) throws Exception;

    public AuthResponse signInUser(SignInRequest loginRequest);

    public Optional<User> updateUser(Long id, SignUpRequest newUser);
    public Optional<User> updateUserWithoutPassword(Long id, EditUserRequest newUser);

    // public User removeUserParticipatingTournament(Long userId, Long tournamentId) throws Exception;
    public List<Tournament> getUserParticipatingTournaments(Long userId) throws Exception;

   
    // public List<Tournament> getUserCurrentParticipatingTournament(Long id);


}