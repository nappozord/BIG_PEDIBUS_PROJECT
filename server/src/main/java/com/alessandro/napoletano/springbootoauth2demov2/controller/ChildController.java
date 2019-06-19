package com.alessandro.napoletano.springbootoauth2demov2.controller;

import com.alessandro.napoletano.springbootoauth2demov2.model.child.Child;
import com.alessandro.napoletano.springbootoauth2demov2.model.child.ChildPutRequest;
import com.alessandro.napoletano.springbootoauth2demov2.model.reservation.ReservationHack;
import com.alessandro.napoletano.springbootoauth2demov2.payload.*;
import com.alessandro.napoletano.springbootoauth2demov2.repository.ChildRepository;
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

    /**
     * Get All the children registerd by the user with this email
     * @param user_email
     * @return
     */
    @GetMapping("/children")
    public ResponseEntity<?> getChildren(@RequestParam(value="user_email", required=false) String user_email){
        List<Child> children;
        if(user_email == null){
            children = childRepository.findAll();
        }else{
            children = childRepository.findChildrenByParent(userRepository.findByEmail(user_email).get());
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand(children).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponseChildren(true, "Here are your children!", children));

    }

    /**
     * Get the child with this name
     * @param name
     * @return
     */
    @GetMapping("/children/{name}")
    public ResponseEntity<?> getChild(@PathVariable("name") String name){

        Child child = childRepository.findChildByChildName(name);

        if (child.getLineDefault() != null) {
            child.getDefaultGoing().getReservations().clear();
            child.getDefaultReturn().getReservations().clear();
            child.getLineDefault().getStopLines_going().clear();
            child.getLineDefault().getStopLines_return().clear();
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand(child).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponseChild(true, "Here are your child!", child));
    }

    @PutMapping("/children/{name}")
    public ResponseEntity<?> putChild(@PathVariable("name") String child_name, @RequestBody ChildPutRequest childPutRequest){

        Child child = childRepository.findChildByChildName(child_name);

        child.setLineDefault(childPutRequest.getLineDefault());
        child.setDefaultGoing(childPutRequest.getDefaultGoing());
        child.setDefaultReturn(childPutRequest.getDefaultReturn());
        child.setChildName(childPutRequest.getChildName());
        child.setParent(childPutRequest.getParent());

        childRepository.save(child);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand(child).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponseChild(true, "Here is your updated child", child));

    }

    /**
     * Get the child reservation with this name
     * @param name
     * @return
     */
    @GetMapping("/children/{name}/reservations")
    public ResponseEntity<?> getChildReservations(@PathVariable("name") String name){

        Child child = childRepository.findChildByChildName(name);

        List<ReservationHack> reservations = new ArrayList<>();

        for(int i = 0; i < child.getReservations().size(); i++){
            ReservationHack reservationHack = new ReservationHack();
            reservationHack.setId(child.getReservations().get(i).getId());
            reservationHack.setStatus(child.getReservations().get(i).getStatus());
            reservationHack.setDate(child.getReservations().get(i).getDate());
            reservationHack.setStopLine(child.getReservations().get(i).getStopLine());
            reservationHack.setChild(child.getChildName());
            reservations.add(reservationHack);
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand(child).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponseReservations(true, "Here are your child!", reservations));

    }
}
