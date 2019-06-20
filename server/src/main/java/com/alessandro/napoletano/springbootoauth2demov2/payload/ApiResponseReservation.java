package com.alessandro.napoletano.springbootoauth2demov2.payload;

import com.alessandro.napoletano.springbootoauth2demov2.model.reservation.ReservationHack;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponseReservation extends ApiResponse {

    private ReservationHack data;

    public ApiResponseReservation(boolean success, ReservationHack data) {
        super(success);
        this.data = data;
    }

    public ApiResponseReservation(boolean success, String message, ReservationHack data) {
        super(success, message);
        this.data = data;
    }
}
