package com.example.api.controller;

import com.example.api.dto.request.UserRequest;
import com.example.api.dto.response.UserResponse;
import com.example.api.entity.User;
import com.example.api.service.UserService;
import com.example.api.state.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getUser(){
        return userService.getUser();
    }

    @GetMapping("/info")
    public ResponseEntity<UserResponse> get(@RequestParam String username){
        User user = userService.getUser(username);
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFirstname(user.getFirstname());
        response.setLastname(user.getLastname());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());

        if(response != null)
            return new ResponseEntity<>(response, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/user-info")
    public Optional<User> getInfo(@AuthenticationPrincipal Jwt jwt){
        String id =  userService.getId(jwt);
        return userService.getUserById(Long.parseLong(id));
    }
    @GetMapping("/hello")
    public String hello(){
        return "Hello angular";
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, String>> check(@RequestParam String username) {
        Map<String, String> response = new HashMap<>();

        UserStatus status = userService.checkUpdate(username);

        if (status == UserStatus.UPDATED) {
            response.put("status", "updated");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else if (status == UserStatus.YET) {
            response.put("status", "yet");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("error", "User not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }


    @PutMapping
    public ResponseEntity<User> update(@AuthenticationPrincipal Jwt jwt, @RequestBody UserRequest request){
        User user = userService.update(jwt, request);

        if(user!=null){
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}