package com.codewithcled.fullstack_backend_proj1.service;

import com.codewithcled.fullstack_backend_proj1.DTO.EditUserRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.SignInRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.SignUpRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.UserDTO;
import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.model.Match;
import com.codewithcled.fullstack_backend_proj1.response.AuthResponse;


import java.util.List;
import java.util.Optional;

/**
 * User Service Interface
 */
public interface UserService {

    /**
     * Retrieves all users.
     * 
     * @return a list of all users.
     */
    public List<User> getAllUser();

    /**
     * Retrieves a user by their email.
     * @param email
     * @return
     */
    public User findUserByEmail(String email) ;

    /**
     * Retrieves a user by their ID.
     * 
     * @param userId the ID of the user to retrieve.
     * @return the user with the specified ID.
     */
    public User findUserById(String userId) ;

    /**
     * Retrieves all users and converts them to DTO objects.
     * 
     * @return the user with the specified username.
     */
    public List<UserDTO> findAllUsersDTO();

    /**
     * Retrieves a user by their username.
     * 
     * @param username the username of the user to retrieve.
     * @return the user with the specified username.
     */
    public User loadByUsername(String username);

    /**
     * Creates a new user.
     * 
     * @param user the sign-up request containing user details.
     * @return an authentication response containing the JWT token.
     * @throws Exception if there is an error during user creation.
     */
    public AuthResponse createUser(SignUpRequest user) throws Exception;

    /**
     * Signs in a user.
     * 
     * @param loginRequest the sign-in request containing login details.
     * @return an authentication response containing the JWT token.
     */
    public AuthResponse signInUser(SignInRequest loginRequest);

    /**
     * Updates a user's details.
     * 
     * @param id the ID of the user to update.
     * @param newUser the new user details.
     * @return an optional containing the updated user, or empty if the user was not found.
     */
    public Optional<User> updateUser(Long id, SignUpRequest newUser);

    /**
     * Updates a user's details without changing the password.
     * 
     * @param id the ID of the user to update.
     * @param newUser the new user details.
     * @return an optional containing the updated user, or empty if the user was not found.
     */
    public Optional<User> updateUserWithoutPassword(Long id, EditUserRequest newUser);

    /**
     * Retrieves the tournaments a user is participating in.
     * 
     * @param userId the ID of the user.
     * @return a list of tournaments the user is participating in.
     * @throws Exception if there is an error retrieving the tournaments.
     */
    public List<Tournament> getUserParticipatingTournaments(Long userId) throws Exception;

    /**
     * Retrieves the past matches of a user.
     * 
     * @param userID the ID of the user.
     * @return a list of past matches of the user.
     * @throws Exception if there is an error retrieving the matches.
     */
    public List<Match> getUserPastMatches(Long userID) throws Exception;

}