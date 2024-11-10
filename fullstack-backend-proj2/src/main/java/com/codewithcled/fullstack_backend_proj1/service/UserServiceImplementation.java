package com.codewithcled.fullstack_backend_proj1.service;

import com.codewithcled.fullstack_backend_proj1.DTO.EditUserRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.SignInRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.SignUpRequest;
import com.codewithcled.fullstack_backend_proj1.DTO.UserDTO;
import com.codewithcled.fullstack_backend_proj1.DTO.UserMapper;
import com.codewithcled.fullstack_backend_proj1.config.JwtProvider;
import com.codewithcled.fullstack_backend_proj1.model.Tournament;
import com.codewithcled.fullstack_backend_proj1.response.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.codewithcled.fullstack_backend_proj1.repository.UserRepository;
import com.codewithcled.fullstack_backend_proj1.repository.MatchRepository;
import com.codewithcled.fullstack_backend_proj1.model.User;
import com.codewithcled.fullstack_backend_proj1.model.Match;
import com.codewithcled.fullstack_backend_proj1.service.UserServiceImplementation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImplementation implements UserService,UserDetailsService {
 
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MatchRepository matchRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if(user==null) {
            throw new UsernameNotFoundException("User not found with this email"+username);
        }
        System.out.println("Loaded user: " + user.getEmail() + ", Role: " + user.getRole());
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole()));
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities);
    }
    // Method to get all users
    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }
    // Method to get user by id
    @Override
    public User findUserProfileByJwt(String jwt) {
        // Implement logic to find user by JWT or remove if not needed
        return null;
    }
    // Method to get user by email
    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    // Method to get user by id
    @Override
    public User findUserById(String userId) {
        // Implement logic to find user by ID or remove if not needed
        return null;
    }
    // Method to update user
    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
    
    // Method to update user
    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> findAllUsersDTO() {
        List<User> users = userRepository.findByRole("ROLE_USER");
        Collections.sort(users,(u1,u2) -> u2.getElo().compareTo(u1.getElo()));
        return UserMapper.toDTOList(users);
    }

    @Override
    public User loadByUsername(String username) {
        return userRepository.findByEmail(username);
    }

    // Helper method to generate a new user object from the given user details
    public User generateUser(SignUpRequest user) {
        User createdUser = new User();
        createdUser.setUsername(user.getUsername());
        createdUser.setEmail(user.getEmail());
        createdUser.setRole(user.getRole());
        createdUser.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole().equals("ROLE_USER")){
            createdUser.setElo(100.0);
        }
        createdUser.setElo(user.getElo());
        return createdUser;
    }
    // Helper method to check if the email is in the correct format
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    public AuthResponse createUser(SignUpRequest user) throws Exception {
        validateUserDetails(user);
        User createdUser = generateUser(user);
        User savedUser = userRepository.save(createdUser);
        authenticateUser(user.getEmail(), user.getPassword());
        String token = generateToken(savedUser);
        return buildAuthResponse(token, "Register Success", true);
    }

    // Helper method to validate the user details
    private void validateUserDetails(SignUpRequest user) throws Exception {
        String email = user.getEmail();
        String username = user.getUsername();

        if (!isValidEmail(email)) {
            throw new Exception("Invalid email format");
        }

        if (userRepository.findByEmail(email) != null) {
            throw new Exception("Email is already used with another account");
        }

        if (userRepository.existsByUsername(username)) {
            throw new Exception("Username is already used with another account");
        }
    }
    // Helper method to authenticate the user
    private void authenticateUser(String email, String password) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    // Helper method to generate a new token for the user
    private String generateToken(User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return JwtProvider.generateToken(authentication, user.getId());
    }
    // Helper method to build the authentication response
    private AuthResponse buildAuthResponse(String token, String message, boolean status) {
        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage(message);
        authResponse.setStatus(status);
        return authResponse;
    }
    
    // Helper method to build the authentication response
    @Override
    public AuthResponse signInUser(SignInRequest loginRequest) {
        Authentication authentication = authenticate(loginRequest.getUsername(), loginRequest.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = userRepository.findByEmail(loginRequest.getUsername());
        String token = generateToken(user);

        return buildAuthResponse(token, "Login success", true, user.getRole());
    }
    // Helper method to build the authentication response
    private AuthResponse buildAuthResponse(String token, String message, boolean status, String role) {
        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage(message);
        authResponse.setStatus(status);
        authResponse.setRole(role);
        return authResponse;
    }
    // Helper method to build the authentication response
    @Override
    public Optional<User> updateUser(Long id, SignUpRequest newUser) {
        
        return userRepository.findById(id)
                .map(user -> {
                    user.setUsername(newUser.getUsername());
                    user.setElo(newUser.getElo());
                    user.setEmail(newUser.getEmail());
                    user.setRole(newUser.getRole());
                    user.setPassword(passwordEncoder.encode(newUser.getPassword()));
                    return userRepository.save(user);  // Save and return updated user
                });
    }

   
    @Override
    public List<Tournament> getUserParticipatingTournaments(Long userId) throws Exception {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));
        return currentUser.getCurrentTournaments();  // Return the list of tournaments the user is participating in
    }

    private Authentication authenticate(String username, String password) {
        UserDetails userDetails = loadUserByUsername(username);
        if(userDetails == null) {
            System.out.println("Sign in details - null" + userDetails);
            throw new BadCredentialsException("Invalid username and password");
        }
        if(!passwordEncoder.matches(password,userDetails.getPassword())) {
            System.out.println("Sign in userDetails - password mismatch"+userDetails);
            throw new BadCredentialsException("Invalid password");
        }
        return new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());

    }

    @Override
    public List<Match> getUserPastMatches(Long userId) throws Exception {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));
        return matchRepository.findByIsCompleteAndPlayer1OrIsCompleteAndPlayer2(true, currentUser.getId(), true, currentUser.getId());  // Return the list of tournaments the user is participating in
    }

    @Override
    public Optional<User> updateUserWithoutPassword(Long id, EditUserRequest newUser) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setUsername(newUser.getUsername());
                    user.setElo(newUser.getElo());
                    user.setRole(newUser.getRole());
                    return userRepository.save(user);  // Save and return updated user
                });
    }

    

}
