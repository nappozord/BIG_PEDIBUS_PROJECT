package com.alessandro.napoletano.springbootoauth2demov2.controller;

import com.alessandro.napoletano.springbootoauth2demov2.model.child.Child;
import com.alessandro.napoletano.springbootoauth2demov2.model.child.ChildHack;
import com.alessandro.napoletano.springbootoauth2demov2.model.reservation.ReservationHack;
import com.alessandro.napoletano.springbootoauth2demov2.model.child.TempForChildDeafult;
import com.alessandro.napoletano.springbootoauth2demov2.payload.ApiResponseChildren;
import com.alessandro.napoletano.springbootoauth2demov2.payload.ApiResponseUser;
import com.alessandro.napoletano.springbootoauth2demov2.repository.ChildRepository;
import com.alessandro.napoletano.springbootoauth2demov2.repository.LineRepository;
import com.alessandro.napoletano.springbootoauth2demov2.repository.StopLineRepository;
import com.alessandro.napoletano.springbootoauth2demov2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ChildController {

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private StopLineRepository stopLineRepository;

    @GetMapping("/children/{user_email}")
    public ResponseEntity<?> getChildren(@PathVariable("user_email") String user_email){

        List<Child> children = childRepository.findChildrenByParent(userRepository.findByEmail(user_email).get());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand(children).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponseChildren(true, "Here are your children!", children));

    }

    @GetMapping("/childrenAndReservations/{user_email}")
    public ResponseEntity<?> getChildrenAndReservations(@PathVariable("user_email") String user_email){

        List<Child> children = childRepository.findChildrenByParent(userRepository.findByEmail(user_email).get());

        List<ChildHack> childrenHack  = new ArrayList<>();

        for(Child child: children){
            ChildHack childHack = new ChildHack();
            childHack.setChild(child);
            List<ReservationHack> reservationHacks = new ArrayList<>();

            for(int i = 0; i < child.getReservations().size(); i++){
                ReservationHack reservationHack = new ReservationHack();
                reservationHack.setId(child.getReservations().get(i).getId());
                reservationHack.setChild(child.getChildName());
                reservationHack.setDate(child.getReservations().get(i).getDate());
                reservationHack.setStatus(child.getReservations().get(i).getStatus());
                reservationHack.setStopLine(child.getReservations().get(i).getStopLine());
                reservationHacks.add(reservationHack);
            }

            childHack.setReservations(reservationHacks);
            childrenHack.add(childHack);
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand(children).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponseChildren(true, "Here are your children!", "OK", childrenHack));

    }

    @GetMapping("/child/{name}")
    public ResponseEntity<?> getChild(@PathVariable("name") String name){

        Child child = childRepository.findChildByChildName(name);

        if (child.getLineDefault() != null) {
            child.getDefaultGoing().getReservations().clear();
            child.getDefaultReturn().getReservations().clear();
            child.getLineDefault().getStopLines_going().clear();
            child.getLineDefault().getStopLines_return().clear();
        }

        List<ReservationHack> reservationHacks = new ArrayList<>();

        for(int i = 0; i < child.getReservations().size(); i++){
            ReservationHack reservationHack = new ReservationHack();
            reservationHack.setId(child.getReservations().get(i).getId());
            reservationHack.setStatus(child.getReservations().get(i).getStatus());
            reservationHack.setDate(child.getReservations().get(i).getDate());
            reservationHack.setStopLine(child.getReservations().get(i).getStopLine());
            reservationHack.setChild(child.getChildName());
            reservationHacks.add(reservationHack);
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand(child).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponseChildren(true, "Here are your children!", child, reservationHacks, "def"));

    }

    @PostMapping("/child/{name}/{line}")
    public ResponseEntity<?> setChildDefault(@PathVariable("name") String child_name, @PathVariable("line") String line_name, @RequestBody TempForChildDeafult tempForChildDeafult){

        Child child = childRepository.findChildByChildName(child_name);

        child.setLineDefault(lineRepository.findByName(line_name));
        child.setDefaultGoing(tempForChildDeafult.getStop_going());
        child.setDefaultReturn(tempForChildDeafult.getStop_return());

        childRepository.save(child);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand(child).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponseUser(true, "Here are your children!"));

    }
}
