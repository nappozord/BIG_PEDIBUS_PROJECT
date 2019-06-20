package com.alessandro.napoletano.springbootoauth2demov2.payload;

import com.alessandro.napoletano.springbootoauth2demov2.model.child.Child;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiResponseChildren extends ApiResponse{
    private List<Child> data;

    public ApiResponseChildren(boolean success, List<Child> data) {
        super(success);
        this.data = data;
    }

    public ApiResponseChildren(boolean success, String message, List<Child> data) {
        super(success, message);
        this.data = data;
    }
}
