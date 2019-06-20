package com.alessandro.napoletano.springbootoauth2demov2.payload;

import com.alessandro.napoletano.springbootoauth2demov2.model.Line;
import com.alessandro.napoletano.springbootoauth2demov2.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiResponseLines {
    private boolean success;
    private String message;
    private List<Line> lines;
    private Line line;
    private List<User> admins;

    public ApiResponseLines(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ApiResponseLines(boolean success, String message, Line line) {
        this.success = success;
        this.message = message;
        this.line = line;
    }

    public ApiResponseLines(boolean success, String message, List<Line> lines) {
        this.success = success;
        this.message = message;
        this.lines = lines;
    }

    public ApiResponseLines(boolean success, String message, Line line, List<User> admins) {
        this.success = success;
        this.message = message;
        this.line = line;
        this.admins = admins;
    }
}
