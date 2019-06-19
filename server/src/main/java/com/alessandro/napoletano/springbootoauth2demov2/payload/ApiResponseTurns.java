package com.alessandro.napoletano.springbootoauth2demov2.payload;

import com.alessandro.napoletano.springbootoauth2demov2.model.turn.TurnHack;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiResponseTurns extends ApiResponse {

    private List<TurnHack> data;

    public ApiResponseTurns(boolean success, List<TurnHack> data) {
        super(success);
        this.data = data;
    }

    public ApiResponseTurns(boolean success, String message, List<TurnHack> data) {
        super(success, message);
        this.data = data;
    }
}
