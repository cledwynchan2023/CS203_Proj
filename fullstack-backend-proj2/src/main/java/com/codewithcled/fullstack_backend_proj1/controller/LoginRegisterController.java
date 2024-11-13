package com.codewithcled.fullstack_backend_proj1.controller;

import org.springframework.beans.factory.annotation.Value;
import com.codewithcled.fullstack_backend_proj1.response.AuthResponse;
import com.codewithcled.fullstack_backend_proj1.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import com.codewithcled.fullstack_backend_proj1.DTO.*;

@RestController
@RequestMapping("/auth")
public class LoginRegisterController {

    // Logger for logging information and warnings
    private static final Logger logger = LoggerFactory.getLogger(LoginRegisterController.class);

    // Injecting the admin token from the application properties
    @Value("${admin.token}")
    private String adminToken;

    // Autowiring the UserService to use its methods
    @Autowired
    private UserService userService;

    /**
     * Endpoint to validate the admin token.
     * 
     * @param tokenRequest The request body containing the token to be validated.
     * @return ResponseEntity with TokenResponse indicating whether the token is
     *         valid or not.
     */
    @PostMapping("/validate-admin-token")
    public ResponseEntity<TokenResponse> validateAdminToken(@RequestBody TokenRequest tokenRequest) {
        logger.info("Validating admin token");
        if (adminToken.equals(tokenRequest.getToken())) {
            return ResponseEntity.ok(new TokenResponse(true));
        } else {
            logger.warn("Invalid admin token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TokenResponse(false));
        }
    }

    // Inner class to represent the response for token validation
    public static class TokenResponse {
        private boolean valid;

        // Constructor to initialize the validity of the token
        public TokenResponse(boolean valid) {
            this.valid = valid;
        }

        // Getter for the valid field
        public boolean isValid() {
            return valid;
        }

        // Setter for the valid field
        public void setValid(boolean valid) {
            this.valid = valid;
        }
    }

    // Inner class to represent the request for token validation
    public static class TokenRequest {
        private String token;

        // Getter for the token field
        public String getToken() {
            return token;
        }

        // Setter for the token field
        public void setToken(String token) {
            this.token = token;
        }
    }

    // Create User
    @SuppressWarnings("null")
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody SignUpRequest user) throws Exception {
        try {
            AuthResponse authResponse = userService.createUser(user);
            return new ResponseEntity<>(authResponse, HttpStatus.CREATED); // Return 201 Created on success
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            return new ResponseEntity<>(null, HttpStatus.CONFLICT); // Return 409 Conflict if username/email is taken
        }
    }

    // Login and authentication
    @PostMapping(value = "/signin", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> signin(@RequestBody SignInRequest loginRequest) {
        AuthResponse authResponse = userService.signInUser(loginRequest);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

}
