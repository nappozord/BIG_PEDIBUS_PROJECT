package com.alessandro.napoletano.springbootoauth2demov2.model.reservation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
@AllArgsConstructor
public class TempForReservation {
    private Long stop_id;

    private Long stopLine_id;

    private String name;

    private String direction;

    private String status;
}
