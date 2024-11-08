package com.example.api.controller;

import com.example.api.entity.User;
import com.example.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

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

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/user-info")
    public User getInfo(@AuthenticationPrincipal Jwt jwt){
        String id =  userService.getId(jwt);
        return userService.getUserById(Integer.parseInt(id));
    }
    @GetMapping("/hello")
    public String hello(){
        return "Hello angular";
    }
}