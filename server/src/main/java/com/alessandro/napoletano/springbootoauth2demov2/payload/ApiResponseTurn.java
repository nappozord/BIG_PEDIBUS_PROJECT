package com.alessandro.napoletano.springbootoauth2demov2.payload;

import com.alessandro.napoletano.springbootoauth2demov2.model.turn.TurnHack;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponseTurn extends ApiResponse {

    private TurnHack data;

    public ApiResponseTurn(boolean success, TurnHack data) {
        super(success);
        this.data = data;
    }

    public ApiResponseTurn(boolean success, String message, TurnHack data) {
        super(success, message);
        this.data = data;
    }
}
