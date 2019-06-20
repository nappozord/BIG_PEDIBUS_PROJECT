package com.alessandro.napoletano.springbootoauth2demov2.payload;

import com.alessandro.napoletano.springbootoauth2demov2.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiResponseUsers extends ApiResponse {
    private List<User> users;

    public ApiResponseUsers(boolean success, List<User> users) {
        super(success);
        this.users = users;
    }

    public ApiResponseUsers(boolean success, String message, List<User> users) {
        super(success, message);
        this.users = users;
    }
}
