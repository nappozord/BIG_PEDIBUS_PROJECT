package com.alessandro.napoletano.springbootoauth2demov2.payload;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiResponseStringList {
    private boolean success;
    private String message;
    private List<String> lines;

    public ApiResponseStringList(boolean success, String message, List<String> lines) {
        this.success = success;
        this.message = message;
        this.lines = lines;
    }
}
