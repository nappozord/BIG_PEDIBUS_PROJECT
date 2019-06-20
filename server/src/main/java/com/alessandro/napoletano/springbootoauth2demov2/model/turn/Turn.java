package com.alessandro.napoletano.springbootoauth2demov2.model.turn;

import com.alessandro.napoletano.springbootoauth2demov2.model.Line;
import com.alessandro.napoletano.springbootoauth2demov2.model.User;
import com.alessandro.napoletano.springbootoauth2demov2.model.reservation.ReservationHack;
import com.alessandro.napoletano.springbootoauth2demov2.model.stopline.StopLine;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Getter
@Setter
public class Turn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn
    @JsonIgnore
    private User user;

    private Date date;

    private String direction;

    private String status;

    @ManyToOne
    @JoinColumn
    private StopLine stopLine_arrival;

    @ManyToOne
    @JoinColumn
    private StopLine stopLine_start;

    @ManyToOne
    @JoinColumn
    @JsonIgnore
    private Line line;

    public Turn() {}

    public TurnHack createTurnHack(){
        TurnHack turnHack = new TurnHack();
        turnHack.setId(this.getId());
        turnHack.setDate(this.getDate());
        turnHack.setUser(this.getUser());
        turnHack.setDirection(this.getDirection());
        turnHack.setStatus(this.getStatus());
        turnHack.setStopLine_arrival(this.getStopLine_arrival());
        turnHack.setStopLine_start(this.getStopLine_start());
        turnHack.setLine(this.getLine());
        return turnHack;
    }
}
