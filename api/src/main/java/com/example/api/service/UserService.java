package com.example.api.service;

import com.example.api.entity.User;
import com.example.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> getUser(){
        return userRepository.findAll();
    }

    public User getUserByUsername(String username){
        return userRepository.findByUsername(username);
    }
    public User getUserById(Integer id){
        return userRepository.findById(id);
    }
    public String getId(@AuthenticationPrincipal Jwt jwt){
        String username = jwt.getClaimAsString("preferred_username");
        return userRepository.findByUsername(username).getId().toString();
    }
}
