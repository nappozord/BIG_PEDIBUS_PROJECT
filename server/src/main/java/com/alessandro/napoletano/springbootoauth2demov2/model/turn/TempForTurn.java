package com.alessandro.napoletano.springbootoauth2demov2.model.turn;

import com.alessandro.napoletano.springbootoauth2demov2.model.Line;
import com.alessandro.napoletano.springbootoauth2demov2.model.stopline.StopLine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
@Setter
@Getter
@AllArgsConstructor
public class TempForTurn {
    private Long idLine;
    private Long stopLineStart;
    private Long stopLineArrival;

    private String personName;
    private String direction;
    private String status;

    private Date date;
}
