package com.alessandro.napoletano.springbootoauth2demov2.controller;

import com.alessandro.napoletano.springbootoauth2demov2.email_verification.OnRegistrationCompleteEvent;
import com.alessandro.napoletano.springbootoauth2demov2.exception.BadRequestException;
import com.alessandro.napoletano.springbootoauth2demov2.model.User;
import com.alessandro.napoletano.springbootoauth2demov2.model.VerificationToken;
import com.alessandro.napoletano.springbootoauth2demov2.payload.ApiResponse;
import com.alessandro.napoletano.springbootoauth2demov2.payload.EmailResendRequest;
import com.alessandro.napoletano.springbootoauth2demov2.repository.UserRepository;
import com.alessandro.napoletano.springbootoauth2demov2.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Calendar;
import java.util.Optional;

@RestController
public class EmailVerificationController {

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @PostMapping("/emailResend")
    public ResponseEntity<?> registerUserConfirmationResend(@RequestBody EmailResendRequest emailResendRequest){

        Optional<User> result = userRepository.findByEmail(emailResendRequest.getEmail());

        verificationTokenRepository.deleteById(verificationTokenRepository.findByUser(result.get()).getId());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand(result.get().getId()).toUri();

        eventPublisher.publishEvent(new OnRegistrationCompleteEvent
                (location, result.get()));

        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "Waiting for email verification@"));
    }
}
