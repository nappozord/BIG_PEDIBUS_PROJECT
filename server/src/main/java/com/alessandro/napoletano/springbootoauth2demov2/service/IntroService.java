package com.alessandro.napoletano.springbootoauth2demov2.service;

import com.alessandro.napoletano.springbootoauth2demov2.email_verification.OnRegistrationCompleteEvent;
import com.alessandro.napoletano.springbootoauth2demov2.exception.BadRequestException;
import com.alessandro.napoletano.springbootoauth2demov2.model.*;
import com.alessandro.napoletano.springbootoauth2demov2.model.stopline.StopLine;
import com.alessandro.napoletano.springbootoauth2demov2.payload.ApiResponseUser;
import com.alessandro.napoletano.springbootoauth2demov2.repository.LineRepository;
import com.alessandro.napoletano.springbootoauth2demov2.repository.UserRepository;
import com.alessandro.napoletano.springbootoauth2demov2.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class IntroService {

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private JavaMailSender mailSender;


    public void save(List<Line> lines){

        List<Stop> stops = new ArrayList<>();
        List<Line> lines_to_save = new ArrayList<>();

        for(Line line : lines){
            Line line1 = new Line(line.getName());
            for(StopLine stopLine : line.getStopLines_going()){
                Stop stop1 = stopLine.getStop();
                int flag = 0;
                for(Stop stop : stops){
                    if(stop.equals(stopLine.getStop())){
                        stop1 = stop;
                        flag = 1;
                    }
                }
                if(flag == 0){
                    stops.add(stop1);
                }
                StopLine stopLine1 = new StopLine();
                stopLine1.setDirection(StopLine.Direction.GOING);
                stopLine1.setHour(stopLine.getHour());
                stopLine1.setLine(line1);
                stopLine1.setStop(stop1);
                line1.getStopLines_going().add(stopLine1);
            }
            for(StopLine stopLine : line.getStopLines_return()){
                Stop stop1 = stopLine.getStop();
                int flag = 0;
                for(Stop stop : stops){
                    if(stop.equals(stopLine.getStop())){
                        stop1 = stop;
                        flag = 1;
                    }
                }
                if(flag == 0){
                    stops.add(stop1);
                }
                StopLine stopLine1 = new StopLine();
                stopLine1.setDirection(StopLine.Direction.RETURN);
                stopLine1.setHour(stopLine.getHour());
                stopLine1.setLine(line1);
                stopLine1.setStop(stop1);
                line1.getStopLines_return().add(stopLine1);
            }
            lines_to_save.add(line1);
        }
        lineRepository.saveAll(lines_to_save);
    }

    public void setLineAdmin(String lineName, String adminEmail){
        Line line = lineRepository.findByName(lineName);
        Optional<User> u = userRepository.findByEmail(adminEmail);

        User user = null;
        // line admin is not in the system yet
        if(line != null && !u.isPresent()){
            user = new User();
            user.setName("TEMP");
            user.setEmail(adminEmail);
            user.setProvider(AuthProvider.local);
            user.setRole("ADMIN");

            User result = userRepository.save(user);

            String token = UUID.randomUUID().toString();
            VerificationToken verificationToken = new VerificationToken(token, result);
            verificationTokenRepository.save(verificationToken);

            String recipientAddress = user.getEmail();
            String subject = "Registration Confirmation";
            String confirmationUrl
                    =  "http://localhost/registrationconfirm?token=" + token;
            String message = "Hi, and welcome in our Pedibus service! \n" +
                    "To confirm your account, please click on the link below.";

            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(recipientAddress);
            email.setSubject(subject);
            email.setText(message + "\n" + confirmationUrl);
            try {
                mailSender.send(email);
                user.getLines().add(line);
            }catch (Exception e){
                verificationTokenRepository.delete(verificationToken);
                userRepository.delete(user);
                System.err.println("Error sending mail");
            }
        }

        if(line != null && u.isPresent()){
            user = u.get();
            user.getLines().add(line);
        }


    }

}
