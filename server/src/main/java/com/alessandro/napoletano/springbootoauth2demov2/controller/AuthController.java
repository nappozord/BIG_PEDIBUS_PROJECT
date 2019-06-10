package com.alessandro.napoletano.springbootoauth2demov2.controller;

import com.alessandro.napoletano.springbootoauth2demov2.exception.BadRequestException;
import com.alessandro.napoletano.springbootoauth2demov2.model.AuthProvider;
import com.alessandro.napoletano.springbootoauth2demov2.model.Line;
import com.alessandro.napoletano.springbootoauth2demov2.model.User;
import com.alessandro.napoletano.springbootoauth2demov2.model.VerificationToken;
import com.alessandro.napoletano.springbootoauth2demov2.payload.ApiResponseUser;
import com.alessandro.napoletano.springbootoauth2demov2.payload.AuthResponse;
import com.alessandro.napoletano.springbootoauth2demov2.payload.LoginRequest;
import com.alessandro.napoletano.springbootoauth2demov2.payload.SignUpRequest;
import com.alessandro.napoletano.springbootoauth2demov2.email_verification.OnRegistrationCompleteEvent;
import com.alessandro.napoletano.springbootoauth2demov2.repository.LineRepository;
import com.alessandro.napoletano.springbootoauth2demov2.repository.UserRepository;
import com.alessandro.napoletano.springbootoauth2demov2.repository.VerificationTokenRepository;
import com.alessandro.napoletano.springbootoauth2demov2.security.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        Optional<User> user = userRepository.findByEmail(loginRequest.getEmail());

        if(!user.get().getEmailVerified()){
            throw new BadRequestException("Email not yet confirmed.");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.createToken(authentication);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/signup_from_admin")
    public ResponseEntity<?> registerUserFromAdmin(@Valid @RequestBody SignUpRequest signUpRequest){
        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("Email address already in use.");
        }

        User user = new User();
        user.setName("TEMP");
        user.setEmail(signUpRequest.getEmail());
        user.setProvider(AuthProvider.local);
        user.setRole(signUpRequest.getRole());
        if(signUpRequest.getLine() != null){
            Line line = lineRepository.findByName(signUpRequest.getLine());
            user.getLines().add(line);
        }

        User result = userRepository.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand(result.getId()).toUri();

        eventPublisher.publishEvent(new OnRegistrationCompleteEvent
                (location, user));

        return ResponseEntity.created(location)
                .body(new ApiResponseUser(true, "Waiting for email verification@"));
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<?> getAllUsers(){

        List<User> users = userRepository.findAll();

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand(users).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponseUser(true, "Here is the list!", users));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest, @RequestParam("token") String token) {

        VerificationToken verificationToken = verificationTokenRepository.findByEmailToken(token);
        if(verificationToken == null){
            throw new BadRequestException("Wrong token");
        }

        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            throw new BadRequestException("Token expired, try again");
        }

        User user = verificationToken.getUser();

        user.setEmailVerified(true);
        user.setName(signUpRequest.getName());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        User result = userRepository.save(user);

        verificationTokenRepository.deleteById(verificationToken.getId());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand(result.getId()).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponseUser(true, "Waiting for email verification@"));
    }
}
