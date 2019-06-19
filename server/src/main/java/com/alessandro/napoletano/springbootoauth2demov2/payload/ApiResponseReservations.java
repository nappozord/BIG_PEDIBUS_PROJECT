package com.alessandro.napoletano.springbootoauth2demov2.payload;

import com.alessandro.napoletano.springbootoauth2demov2.model.reservation.ReservationHack;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiResponseReservations extends ApiResponse {

    private List<ReservationHack> data;

    public ApiResponseReservations(boolean success, List<ReservationHack> data) {
        super(success);
        this.data = data;
    }

    public ApiResponseReservations(boolean success, String message, List<ReservationHack> data) {
        super(success, message);
        this.data = data;
    }
}
