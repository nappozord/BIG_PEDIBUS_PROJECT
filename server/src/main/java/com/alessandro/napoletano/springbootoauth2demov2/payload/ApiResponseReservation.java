package com.alessandro.napoletano.springbootoauth2demov2.payload;

import com.alessandro.napoletano.springbootoauth2demov2.model.child.Child;
import com.alessandro.napoletano.springbootoauth2demov2.model.reservation.ReservationHack;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiResponseReservation {
    private boolean success;
    private String message;
    private Long id;
    private List<ReservationHack> reservation_going;
    private List<ReservationHack> reservation_return;
    private List<Child> child_going;
    private List<Child> child_return;

    public ApiResponseReservation(boolean success, String message, Long id) {
        this.success = success;
        this.message = message;
        this.id = id;
    }

    public ApiResponseReservation(boolean success, String message, List<ReservationHack> reservation_going,
                  List<ReservationHack> reservation_return, List<Child> child_going, List<Child> child_return) {
        this.success = success;
        this.message = message;
        this.reservation_going = reservation_going;
        this.reservation_return = reservation_return;
        this.child_going = child_going;
        this.child_return = child_return;
    }
}
