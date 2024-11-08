package com.example.api.controller;

import com.example.api.dto.request.LoginRequest;
import com.example.api.dto.request.RegisterRequest;
import com.example.api.dto.response.ApiResponse;
import com.example.api.dto.response.TokenResponse;
import com.example.api.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    public AuthService authService;


    @PostMapping("/login")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        Map<String, Object> tokenResponse = authService.getToken(request.getUsername(), request.getPassword());

        if (tokenResponse != null && tokenResponse.containsKey("access_token")) {
            TokenResponse response = new TokenResponse(
                    (String) tokenResponse.get("access_token"),
                    (String) tokenResponse.get("refresh_token"),
                    (Integer) tokenResponse.get("expires_in"),
                    (String) tokenResponse.get("token_type"),
                    (String) tokenResponse.get("scope"),
                    (String) tokenResponse.get("session_state")
            );
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(String username,String password,
                                            String email,String firstname,
                                            String lastname){
        authService.register(username, password, email, firstname, lastname);

        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/register-user")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody RegisterRequest request){
            authService.registerUser(request);
            ApiResponse response = new ApiResponse("Register User successfully");
            return ResponseEntity.ok(response);
    }
    @PostMapping("/check-update")
    @CrossOrigin(origins = "http://localhost:4200")
    public boolean checkUpdate(@RequestBody String username) {
        return authService.checkUpdate(username);
    }
}
