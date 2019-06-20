package com.alessandro.napoletano.springbootoauth2demov2.payload;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiResponseLinesNames extends ApiResponse{
    private List<String> data;

    public ApiResponseLinesNames(boolean success, List<String> data) {
        super(success);
        this.data = data;
    }

    public ApiResponseLinesNames(boolean success, String message, List<String> data) {
        super(success, message);
        this.data = data;
    }
}
