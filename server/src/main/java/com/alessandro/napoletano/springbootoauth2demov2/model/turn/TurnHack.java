package com.alessandro.napoletano.springbootoauth2demov2.model.turn;

import com.alessandro.napoletano.springbootoauth2demov2.model.Line;
import com.alessandro.napoletano.springbootoauth2demov2.model.User;
import com.alessandro.napoletano.springbootoauth2demov2.model.stopline.StopLine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Data
@AllArgsConstructor
public class TurnHack {
    private Long id;

    private Date date;

    private User user;

    private String status;

    private String direction;

    private Line line;

    private StopLine stopLine_start;

    private StopLine stopLine_arrival;

    public TurnHack() { }
}
