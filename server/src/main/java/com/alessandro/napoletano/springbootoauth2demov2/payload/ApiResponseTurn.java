package com.alessandro.napoletano.springbootoauth2demov2.payload;

import com.alessandro.napoletano.springbootoauth2demov2.model.turn.Turn;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiResponseTurn {
    private boolean success;
    private String message;
    private List<Turn> turns;
    private List<String> users;

    public ApiResponseTurn(boolean success, String message, List<Turn> turns) {
        this.message = message;
        this.success = success;
        this.turns = turns;
    }

    public ApiResponseTurn(boolean success, String message, List<Turn> turns, List<String> users) {
        this.message = message;
        this.success = success;
        this.turns = turns;
        this.users = users;
    }
}
