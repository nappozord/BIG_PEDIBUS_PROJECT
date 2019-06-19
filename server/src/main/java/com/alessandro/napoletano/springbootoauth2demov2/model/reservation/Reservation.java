package com.alessandro.napoletano.springbootoauth2demov2.model.reservation;

import com.alessandro.napoletano.springbootoauth2demov2.model.child.Child;
import com.alessandro.napoletano.springbootoauth2demov2.model.stopline.StopLine;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Data
@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn
    @JsonIgnore
    private Child child;

    private String date;

    private String status;

    private Boolean reserved;

    @ManyToOne
    @JoinColumn
    @JsonIgnore
    private StopLine stopLine;

    public Reservation() {}

    public ReservationHack createReservationHack(){
        ReservationHack reservationHack = new ReservationHack();
        reservationHack.setId(this.getId());
        reservationHack.setStatus(this.getStatus());
        reservationHack.setDate(this.getDate());
        reservationHack.setStopLine(this.getStopLine());
        reservationHack.setChild(this.getChild().getChildName());
        reservationHack.setParent(this.getChild().getParent().getEmail());
        reservationHack.setReserved(this.getReserved());
        return reservationHack;
    }
}
