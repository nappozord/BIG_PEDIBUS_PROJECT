package com.alessandro.napoletano.springbootoauth2demov2.payload;

import com.alessandro.napoletano.springbootoauth2demov2.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponseUser extends ApiResponse {
    private User user;

    public ApiResponseUser(boolean success, User user) {
        super(success);
        this.user = user;
    }

    public ApiResponseUser(boolean success, String message, User user) {
        super(success, message);
        this.user = user;
    }
}
