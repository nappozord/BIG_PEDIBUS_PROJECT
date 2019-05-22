package com.alessandro.napoletano.springbootoauth2demov2.password_reset;

import com.alessandro.napoletano.springbootoauth2demov2.model.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.net.URI;

@Getter
@Setter
public class OnPasswordResetEvent extends ApplicationEvent {
    private URI location;
    private User user;
    private String password;

    public OnPasswordResetEvent(URI location, User user, String password) {
        super(user);
        this.location = location;
        this.user = user;
        this.password = password;
    }
}
