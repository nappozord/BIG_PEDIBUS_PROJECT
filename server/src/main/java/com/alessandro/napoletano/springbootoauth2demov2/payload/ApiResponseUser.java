package com.alessandro.napoletano.springbootoauth2demov2.payload;

import com.alessandro.napoletano.springbootoauth2demov2.model.Line;
import com.alessandro.napoletano.springbootoauth2demov2.model.User;
import com.alessandro.napoletano.springbootoauth2demov2.model.turn.TurnHack;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiResponseUser {
    private boolean success;
    private String message;
    private String email;
    private User user;
    private List<User> users;
    private List<TurnHack> turnHacks;

    public ApiResponseUser(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ApiResponseUser(boolean success, String message, String email) {
        this.success = success;
        this.message = message;
        this.email = email;
    }

    public ApiResponseUser(boolean success, String message, List<User> users) {
        this.success = success;
        this.message = message;
        this.users = users;
    }

    public ApiResponseUser(boolean success, String message, User user, List<TurnHack> turnHacks) {
        this.success = success;
        this.message = message;
        this.user = user;
        this.turnHacks = turnHacks;
    }
}
