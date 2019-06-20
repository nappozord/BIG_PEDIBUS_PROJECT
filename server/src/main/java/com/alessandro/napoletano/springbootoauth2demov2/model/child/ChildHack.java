package com.alessandro.napoletano.springbootoauth2demov2.model.child;

import com.alessandro.napoletano.springbootoauth2demov2.model.reservation.ReservationHack;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChildHack {
    private Child child;

    private List<ReservationHack> reservations;


}
