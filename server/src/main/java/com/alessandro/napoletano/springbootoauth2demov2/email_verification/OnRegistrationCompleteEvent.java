package com.alessandro.napoletano.springbootoauth2demov2.email_verification;

import com.alessandro.napoletano.springbootoauth2demov2.model.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.net.URI;

@Getter
@Setter
public class OnRegistrationCompleteEvent extends ApplicationEvent {
    private URI location;
    private User user;

    public OnRegistrationCompleteEvent(URI location, User user) {
        super(user);
        this.location = location;
        this.user = user;
    }
}
