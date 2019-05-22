package com.alessandro.napoletano.springbootoauth2demov2.model.turn;

import com.alessandro.napoletano.springbootoauth2demov2.model.Line;
import com.alessandro.napoletano.springbootoauth2demov2.model.stopline.StopLine;
import com.alessandro.napoletano.springbootoauth2demov2.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

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

    private String date;

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
}
