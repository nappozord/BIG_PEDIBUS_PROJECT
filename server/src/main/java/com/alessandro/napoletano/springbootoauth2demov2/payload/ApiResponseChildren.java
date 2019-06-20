package com.alessandro.napoletano.springbootoauth2demov2.payload;

import com.alessandro.napoletano.springbootoauth2demov2.model.child.Child;
import com.alessandro.napoletano.springbootoauth2demov2.model.child.ChildHack;
import com.alessandro.napoletano.springbootoauth2demov2.model.reservation.ReservationHack;
import com.alessandro.napoletano.springbootoauth2demov2.model.stopline.StopLine;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiResponseChildren {
    private boolean success;
    private String message;
    private List<Child> children;
    private Child child;
    private List<StopLine> stopLines;
    private String name;
    private List<ReservationHack> reservationHacks;
    private List<ChildHack> childHacks;

    public ApiResponseChildren(boolean success, String message, List<Child> children) {
        this.success = success;
        this.message = message;
        this.children = children;
    }

    public ApiResponseChildren(boolean success, String message, Child child) {
        this.success = success;
        this.message = message;
        this.child = child;
    }

    public ApiResponseChildren(boolean success, String message, Child child, List<StopLine> stopLines) {
        this.success = success;
        this.message = message;
        this.child = child;
        this.stopLines = stopLines;
    }

    public ApiResponseChildren(boolean success, String message, Child child, List<ReservationHack> reservationHacks, String name) {
        this.success = success;
        this.message = message;
        this.child = child;
        this.reservationHacks = reservationHacks;
        this.name = name;
    }

    public ApiResponseChildren(boolean success, String message, String name, List<ChildHack> childHacks) {
        this.success = success;
        this.message = message;
        this.name = name;
        this.childHacks = childHacks;
    }
}
