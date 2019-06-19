package com.alessandro.napoletano.springbootoauth2demov2.payload;

import com.alessandro.napoletano.springbootoauth2demov2.model.Line;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponseLine extends ApiResponse{
    private Line data;

    public ApiResponseLine(boolean success, Line data) {
        super(success);
        this.data = data;
    }

    public ApiResponseLine(boolean success, String message, Line data) {
        super(success, message);
        this.data = data;
    }
}
