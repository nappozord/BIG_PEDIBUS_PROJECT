package com.alessandro.napoletano.springbootoauth2demov2.payload;

import com.alessandro.napoletano.springbootoauth2demov2.model.Line;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiResponseLines extends ApiResponse{
    private List<Line> data;

    public ApiResponseLines(boolean success, List<Line> data) {
        super(success);
        this.data = data;
    }

    public ApiResponseLines(boolean success, String message, List<Line> data) {
        super(success, message);
        this.data = data;
    }
}
