package com.alessandro.napoletano.springbootoauth2demov2.model.stopline;

import com.alessandro.napoletano.springbootoauth2demov2.model.Line;
import com.alessandro.napoletano.springbootoauth2demov2.model.Stop;
import com.alessandro.napoletano.springbootoauth2demov2.model.reservation.Reservation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Data
@Entity
public class StopLine {

    public enum Direction{
        GOING,
        RETURN
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String hour;

    @Enumerated(EnumType.STRING)
    private Direction direction;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private Stop stop;

    @ManyToOne
    @JoinColumn
    @JsonIgnore
    private Line line;

    @OneToMany(mappedBy = "stopLine", orphanRemoval = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Reservation> reservations;

    public StopLine(Stop stop) {
        this.stop = stop;
    }

    public StopLine() {}
}
