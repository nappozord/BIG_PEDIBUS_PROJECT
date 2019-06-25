package com.alessandro.napoletano.springbootoauth2demov2.model.turn;

import com.alessandro.napoletano.springbootoauth2demov2.model.stopline.StopLine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
@AllArgsConstructor
public class TempForTurn {
    private StopLine stopLine_start;

    private StopLine stopLine_arrival;

    //email
    private String name;

    private String direction;

    private String status;
}
