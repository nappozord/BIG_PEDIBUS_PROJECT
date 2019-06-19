package com.alessandro.napoletano.springbootoauth2demov2.controller;

import com.alessandro.napoletano.springbootoauth2demov2.exception.BadRequestException;
import com.alessandro.napoletano.springbootoauth2demov2.model.PasswordResetToken;
import com.alessandro.napoletano.springbootoauth2demov2.model.User;
import com.alessandro.napoletano.springbootoauth2demov2.password_reset.OnPasswordResetEvent;
import com.alessandro.napoletano.springbootoauth2demov2.payload.ApiResponse;
import com.alessandro.napoletano.springbootoauth2demov2.payload.PasswordResetRequest;
import com.alessandro.napoletano.springbootoauth2demov2.repository.PasswordTokenRepository;
import com.alessandro.napoletano.springbootoauth2demov2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Calendar;
import java.util.Optional;

@RestController
public class PasswordResetController  {

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordTokenRepository passwordTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/passwordRecovery")
    public ResponseEntity<?> passwordRecovery(@RequestBody PasswordResetRequest passwordResetRequest){

        if(!userRepository.existsByEmail(passwordResetRequest.getEmail())) {
            throw new BadRequestException("Email does not exist.");
        }

        Optional<User> result = userRepository.findByEmail(passwordResetRequest.getEmail());

        if(!result.get().getProvider().toString().equals("local")) {
            throw new BadRequestException("Email does not exist (you have logged in with " + result.get().getProvider().toString() + ").");
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand(result.get().getId()).toUri();

        eventPublisher.publishEvent(new OnPasswordResetEvent
                (location, result.get(), passwordResetRequest.getPassword()));

        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "Waiting for email verification@"));
    }

    @GetMapping("/recoveryConfirm")
    public ResponseEntity<?> resetPasswordConfirmation(@RequestParam("token") String token){

        PasswordResetToken passwordResetToken = passwordTokenRepository.findByToken(token);
        if(passwordResetToken == null){
            throw new BadRequestException("Wrong token!");
        }

        User user = passwordResetToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((passwordResetToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            throw new BadRequestException("Token expired, try again!");
        }

        user.setPassword(passwordEncoder.encode(passwordResetToken.getPassword()));

        User result = userRepository.save(user);

        passwordTokenRepository.deleteById(passwordResetToken.getId());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand(result.getId()).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "User registered successfully@"));
    }
}
