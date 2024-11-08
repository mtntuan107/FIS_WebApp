package com.example.api.service;

import com.example.api.dto.request.RegisterRequest;
import com.example.api.entity.User;
import com.example.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class AuthService {
    private UserRepository userRepository;

    private BCryptPasswordEncoder bCryptPasswordEncoder;


    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    public boolean login(String username, String password){
        User user = userRepository.findByUsername(username);
        if(user != null) {
            return bCryptPasswordEncoder.matches(password, user.getPassword());
        }
        return false;
    }


    public Map<String, Object> getToken(String username, String password) {
        String keycloakUrl = "http://localhost:8080/realms/custom/protocol/openid-connect/token";
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", "custom");
        params.add("username", username);
        params.add("password", password);
        params.add("grant_type", "password");

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.exchange(keycloakUrl, HttpMethod.POST, entity, Map.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                return responseBody;
            }
        }
        return null;
    }

    public User register(String username, String password, String email, String firstname, String lastname){
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        String encodedPassword = bCryptPasswordEncoder.encode(password);
        user.setPassword(encodedPassword);

        return userRepository.save(user);
    }

    public User registerUser(RegisterRequest request){
        if(userRepository.findByUsername(request.getUsername())!=null){
            throw new IllegalArgumentException("Username is exists");
        }
        String encodedPassword = bCryptPasswordEncoder.encode(request.getPassword());

        User user = new User();

        user.setUsername(request.getUsername());
        user.setPassword(encodedPassword);

        return userRepository.save(user);
    }

    public boolean checkUpdate(String username){
        if(userRepository.findByUsername(username)!= null){
            if(userRepository.findByUsername(username).getEmail()==null){
                return true;
            }
            return false;
        }
        return false;
    }
}
