package com.alessandro.napoletano.springbootoauth2demov2.controller;

import com.alessandro.napoletano.springbootoauth2demov2.exception.BadRequestException;
import com.alessandro.napoletano.springbootoauth2demov2.model.AuthProvider;
import com.alessandro.napoletano.springbootoauth2demov2.model.Line;
import com.alessandro.napoletano.springbootoauth2demov2.model.User;
import com.alessandro.napoletano.springbootoauth2demov2.model.reservation.Reservation;
import com.alessandro.napoletano.springbootoauth2demov2.payload.*;
import com.alessandro.napoletano.springbootoauth2demov2.email_verification.OnRegistrationCompleteEvent;
import com.alessandro.napoletano.springbootoauth2demov2.repository.LineRepository;
import com.alessandro.napoletano.springbootoauth2demov2.repository.ReservationRepository;
import com.alessandro.napoletano.springbootoauth2demov2.repository.UserRepository;
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
    private TokenProvider tokenProvider;

    @Autowired
    private ReservationRepository reservationRepository;

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
            user.getAdministeredLines().add(line);
        }

        User result = userRepository.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand(result.getId()).toUri();

        eventPublisher.publishEvent(new OnRegistrationCompleteEvent
                (location, user));

        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "Waiting for email verification@"));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("Email address already in use.");
        }

        // Creating user's account
        User user = new User();
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(signUpRequest.getPassword());
        user.setProvider(AuthProvider.local);
        user.setEmailVerified(true);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User result = userRepository.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand(result.getId()).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "Ok"));
    }

    @GetMapping("/getAllConfirmedUsers")
    public ResponseEntity<?> getAllConfirmedUsers(){

        List<User> users = userRepository.findAllByEmailVerifiedTrue();

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand(users).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponseUsers(true, "Here is the list!", users));
    }
}
