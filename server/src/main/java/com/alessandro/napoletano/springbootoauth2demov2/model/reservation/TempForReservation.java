package com.alessandro.napoletano.springbootoauth2demov2.model.reservation;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class TempForReservation {
    private Long stopLine_id;

    private String child;

    private String date;

    private Boolean reserved;

    private String status;

    public TempForReservation() {}

    public TempForReservation(Long stopLine_id, String child, String date, Boolean reserved, String status) {
        this.stopLine_id = stopLine_id;
        this.child = child;
        this.date = date;
        this.reserved = reserved;
        this.status = status;
    }
}
