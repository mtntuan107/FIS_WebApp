package com.example.api.service;

import com.example.api.dto.request.UserRequest;
import com.example.api.entity.User;
import com.example.api.repository.UserRepository;
import com.example.api.state.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(){
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    public List<User> getUser(){
        return userRepository.findAll();
    }

    public User getUserByUsername(String username){
        return userRepository.findByUsername(username);
    }
    public Optional<User> getUserById(Long id){
        return userRepository.findById(id);
    }
    public String getId(@AuthenticationPrincipal Jwt jwt){
        String username = jwt.getClaimAsString("preferred_username");
        return userRepository.findByUsername(username).getId().toString();
    }

    public User getUser(String username){
        return userRepository.findByUsername(username);
    }

    public Long getIdLong(@AuthenticationPrincipal Jwt jwt){
        String username = jwt.getClaimAsString("preferred_username");
        return userRepository.findByUsername(username).getId();
    }

    public UserStatus checkUpdate(String username){
        User user = userRepository.findByUsername(username);
        if(user!=null){
            if(user.getEmail()!=null){
                return UserStatus.UPDATED;
            }
            return UserStatus.YET;
        }
        return UserStatus.NOT_EXIST;
    }

    public User update(@AuthenticationPrincipal Jwt jwt, UserRequest request){
        User user = userRepository.findById(getIdLong(jwt)).get();
        if(request.getFirstname()!= null)
            user.setFirstname(request.getFirstname());
        if(request.getLastname()!= null)
            user.setLastname(request.getLastname());
        if(request.getPassword() != null)
            user.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));

        return userRepository.save(user);
    }

    public User updateProfile(Long userId, UserRequest request){
        User user = userRepository.findById(userId).get();
        if(request.getFirstname()!= null)
            user.setFirstname(request.getFirstname());
        if(request.getLastname()!= null)
            user.setLastname(request.getLastname());
        return userRepository.save(user);
    }
}
