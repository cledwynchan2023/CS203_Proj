package com.codewithcled.fullstack_backend_proj1.controller;

import org.springframework.beans.factory.annotation.Value;
import com.codewithcled.fullstack_backend_proj1.response.AuthResponse;
import com.codewithcled.fullstack_backend_proj1.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import com.codewithcled.fullstack_backend_proj1.DTO.*;


@RestController
@RequestMapping("/auth")
public class LoginRegisterController {


    @Value("${admin.token}")
    private String adminToken;

    @Autowired
    private UserService userService;

    @PostMapping("/validate-admin-token")
    public ResponseEntity<?> validateAdminToken(@RequestBody TokenRequest tokenRequest) {
        if (adminToken.equals(tokenRequest.getToken())) {
            return ResponseEntity.ok(new TokenResponse(true));
        } else {
            return ResponseEntity.status(401).body(new TokenResponse(false));
        }
    }
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

    // Create User to Database
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody SignUpRequest user) throws Exception {

        try {
            AuthResponse authResponse = userService.createUser(user);
            return new ResponseEntity<>(authResponse, HttpStatus.CREATED);  // Return 201 Created on success
        } catch (Exception ex) {
            System.out.println("EROR!");
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);  // Return 409 Conflict if username/email is taken
        }
//        catch (Exception ex) {
//            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);  // Return 500 for other errors
//        }

    }


    //Login and authentication
    @PostMapping(value = "/signin", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> signin(@RequestBody SignInRequest loginRequest) {
        AuthResponse authResponse = userService.signInUser(loginRequest);
        return new ResponseEntity<>(authResponse,HttpStatus.OK);
    }
   

}

