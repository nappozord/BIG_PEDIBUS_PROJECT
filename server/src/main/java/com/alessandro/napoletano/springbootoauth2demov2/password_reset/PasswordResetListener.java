package com.alessandro.napoletano.springbootoauth2demov2.password_reset;

import com.alessandro.napoletano.springbootoauth2demov2.model.PasswordResetToken;
import com.alessandro.napoletano.springbootoauth2demov2.model.User;
import com.alessandro.napoletano.springbootoauth2demov2.model.VerificationToken;
import com.alessandro.napoletano.springbootoauth2demov2.repository.PasswordTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PasswordResetListener implements ApplicationListener<OnPasswordResetEvent> {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordTokenRepository passwordTokenRepository;

    @Override
    public void onApplicationEvent(OnPasswordResetEvent event) {
        this.ResetPassword(event);
    }

    private void ResetPassword(OnPasswordResetEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user, event.getPassword());
        passwordTokenRepository.save(passwordResetToken);

        String recipientAddress = user.getEmail();
        String subject = "Password Reset";
        String confirmationUrl
                =  "http://nappozord.tk/recoveryconfirm?token=" + token;
        String message = "HELLO! CONFIRM YOUR PASSWORD RESET PLS!!!";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + "\n" + confirmationUrl);
        mailSender.send(email);
    }

}
