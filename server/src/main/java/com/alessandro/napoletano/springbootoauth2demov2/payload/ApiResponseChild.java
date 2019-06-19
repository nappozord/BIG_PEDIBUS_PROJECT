package com.alessandro.napoletano.springbootoauth2demov2.payload;

import com.alessandro.napoletano.springbootoauth2demov2.model.child.Child;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponseChild extends ApiResponse{
    private Child data;

    public ApiResponseChild(boolean success, Child data) {
        super(success);
        this.data = data;
    }

    public ApiResponseChild(boolean success, String message, Child data) {
        super(success, message);
        this.data = data;
    }
}
