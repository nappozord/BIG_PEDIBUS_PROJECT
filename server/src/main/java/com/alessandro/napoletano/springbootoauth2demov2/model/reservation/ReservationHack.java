package com.alessandro.napoletano.springbootoauth2demov2.model.reservation;

import com.alessandro.napoletano.springbootoauth2demov2.model.stopline.StopLine;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@AllArgsConstructor
public class ReservationHack {

    private Long id;

    private String date;

    private String status;

    private Boolean reserved;

    private StopLine stopLine;

    private String child;

    private String parent;

    public ReservationHack() { }
}
