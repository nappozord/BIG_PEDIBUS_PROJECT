package com.alessandro.napoletano.springbootoauth2demov2.controller;

import com.alessandro.napoletano.springbootoauth2demov2.exception.ResourceNotFoundException;
import com.alessandro.napoletano.springbootoauth2demov2.model.User;
import com.alessandro.napoletano.springbootoauth2demov2.payload.ApiResponseUser;
import com.alessandro.napoletano.springbootoauth2demov2.payload.ApiResponseUsers;
import com.alessandro.napoletano.springbootoauth2demov2.repository.UserRepository;
import com.alessandro.napoletano.springbootoauth2demov2.security.CurrentUser;
import com.alessandro.napoletano.springbootoauth2demov2.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user/me")
    public User getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    }


    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(){

        List<User> users = userRepository.findAll();

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand(users).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponseUsers(true, "Here is the list!", users));
    }
}
