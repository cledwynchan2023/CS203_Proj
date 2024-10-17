package com.codewithcled.fullstack_backend_proj1.controller;

import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;
import com.codewithcled.fullstack_backend_proj1.service.EloRatingServiceImplementation;
import com.codewithcled.fullstack_backend_proj1.service.UserService;
import com.codewithcled.fullstack_backend_proj1.service.UserServiceImplementation;
import com.codewithcled.fullstack_backend_proj1.DTO.EditUserRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.SignUpRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.TournamentDTO;
import com.codewithcled.fullstack_backend_proj1.DTO.TournamentMapper;
import com.codewithcled.fullstack_backend_proj1.DTO.UserDTO;
import com.codewithcled.fullstack_backend_proj1.DTO.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;

/*
 * User Controller
 * Authorisation includes:
 *  - Edit User
 *  - Delete User
 *  - Get User by ID
 *  - Get User by Username
 *  - Get all Users
 *  - Add User to Participating Tournament
 *  - Remove User from Participating Tournament
 *  - Get User Participating Tournaments
 *  
 */

@RestController
@RequestMapping("/u")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    //@Autowired
    //private TournamentService tournamentService;


    @Autowired
    private UserService userService;

    public static class TokenResponse {
        private boolean valid;

        public TokenResponse(boolean valid) {
            this.valid = valid;
        }

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }
    }

    public static class TokenRequest {
        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    // GET FUNCTIONS
    // Get all users
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();  // Return 204 No Content if the list is empty
        }
        List<UserDTO> userDTOs = UserMapper.toDTOList(users);
        return ResponseEntity.ok(userDTOs);  // Return 200 OK with the list of UserDTOs
    }

    // Get User by Username
    @GetMapping("/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable("username") String email) {
        try {
            User newUser = userRepository.findByEmail(email);
            UserDTO userDto = UserMapper.toDTO(newUser);
            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
    }

    // Get User Current Participating Tournaments
    @GetMapping("/{id}/currentTournament")
    public ResponseEntity<List<TournamentDTO>> getUserParticipatingTournaments(@PathVariable("id") Long id) {
        try {
            User user = userRepository.findById(id).orElseThrow(() -> new Exception("User not found"));
            List<TournamentDTO> tournamentDTOs = TournamentMapper.toDTOList(user.getCurrentTournaments());
            return ResponseEntity.ok(tournamentDTOs);  // Return 200 OK with the list of TournamentDTOs
        } catch (Exception e) {
            // Log the exception message for debugging
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);  // Return 400 Bad Request for errors
        }
    }

    // Get User by ID
    @GetMapping("/id/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") Long id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(UserMapper.toDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    //delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser (@PathVariable("id") Long id){
        if (!userRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  // Return 404 if user not found
        }
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();  // Return 204 No Content on successful deletion
    }



    //editing profile
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@RequestBody SignUpRequest newUser, @PathVariable("id") Long id) {
        return userService.updateUser(id, newUser)
                .map(updatedUser -> {
                    UserDTO userDTO = UserMapper.toDTO(updatedUser);
                    return ResponseEntity.ok(userDTO);  // Return 200 OK with the updated UserDTO
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());  // Return 404 if user not found
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<UserDTO> updateUserWithoutPassword (@RequestBody EditUserRequest newUser, @PathVariable("id") Long id) {
        return userService.updateUserWithoutPassword(id, newUser)
                .map(updatedUser -> {
                    UserDTO userDTO = UserMapper.toDTO(updatedUser);
                    return ResponseEntity.ok(userDTO);  // Return 200 OK with the updated UserDTO
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());  // Return 404 if user not found
    }

    


}
