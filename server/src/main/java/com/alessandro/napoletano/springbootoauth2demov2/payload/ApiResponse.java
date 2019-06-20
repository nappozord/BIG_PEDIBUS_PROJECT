package com.alessandro.napoletano.springbootoauth2demov2.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse {
    protected boolean success;
    protected String message;

    public ApiResponse(boolean success) {
        this.success = success;
    }

    public ApiResponse(boolean success, String message) {
        this(success);
        this.message = message;
    }
}
