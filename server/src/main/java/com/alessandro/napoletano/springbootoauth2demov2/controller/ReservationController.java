package com.alessandro.napoletano.springbootoauth2demov2.controller;

import com.alessandro.napoletano.springbootoauth2demov2.model.Message;
import com.alessandro.napoletano.springbootoauth2demov2.model.child.Child;
import com.alessandro.napoletano.springbootoauth2demov2.model.reservation.Reservation;
import com.alessandro.napoletano.springbootoauth2demov2.model.reservation.ReservationHack;
import com.alessandro.napoletano.springbootoauth2demov2.model.stopline.StopLine;
import com.alessandro.napoletano.springbootoauth2demov2.model.reservation.TempForReservation;
import com.alessandro.napoletano.springbootoauth2demov2.payload.ApiResponseReservation;
import com.alessandro.napoletano.springbootoauth2demov2.payload.ApiResponseUser;
import com.alessandro.napoletano.springbootoauth2demov2.repository.ChildRepository;
import com.alessandro.napoletano.springbootoauth2demov2.repository.MessageRepository;
import com.alessandro.napoletano.springbootoauth2demov2.repository.ReservationRepository;
import com.alessandro.napoletano.springbootoauth2demov2.repository.StopLineRepository;
import com.alessandro.napoletano.springbootoauth2demov2.security.CurrentUser;
import com.alessandro.napoletano.springbootoauth2demov2.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class ReservationController {

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private StopLineRepository stopLineRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private WebSocketController webSocketController;

    @PostMapping("reservations/{nome_linea}/{data}")
    public ResponseEntity<?> postReservation(@PathVariable("nome_linea") String line_name, @PathVariable("data") String date, @RequestBody TempForReservation tempForReservation, @CurrentUser UserPrincipal userPrincipal){
        StopLine stopLine = stopLineRepository.findStopLineByLineNameAndDirectionAndStopId(line_name, tempForReservation.getDirection(), tempForReservation.getStop_id());
        Reservation reservation = new Reservation();
        reservation.setStatus(tempForReservation.getStatus());
        reservation.setChild(childRepository.findChildByChildName(tempForReservation.getName()));
        reservation.setDate(date);
        reservation.setStopLine(stopLine);
        reservation.setStatus(tempForReservation.getStatus());
        reservationRepository.save(reservation);

        if(reservation.getStatus() != null){
            Message message = new Message();
            message.setFromUser(userPrincipal.getEmail());
            message.setToUser(reservation.getChild().getParent().getEmail());
            message.setMess(userPrincipal.getEmail() + " has taken your child " + reservation.getChild().getChildName() + " :)");
            message.setUser(reservation.getChild().getParent());
            message.setRead(false);
            messageRepository.save(message);

            if(!message.getToUser().equals(message.getFromUser())){
                try {
                    webSocketController.sendSpecific(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand(reservation).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponseReservation(true, "Reservation succeeded!", reservation.getId()));
    }

    @GetMapping("reservations/{nome_linea}/{data}")
    public ResponseEntity<?> getStopLinesWithReservations(@PathVariable("nome_linea") String line_name, @PathVariable("data") String date){
        List<Reservation> reservation_goingX = reservationRepository.findReservationByLineName(line_name, "GOING", date);
        List<ReservationHack> reservation_going = new ArrayList<>();

        for(Reservation reservation: reservation_goingX){
            ReservationHack reservationHack = new ReservationHack();
            reservationHack.setId(reservation.getId());
            reservationHack.setStatus(reservation.getStatus());
            reservationHack.setDate(reservation.getDate());
            reservationHack.setStopLine(reservation.getStopLine());
            reservationHack.setChild(reservation.getChild().getChildName());
            reservationHack.setParent(reservation.getChild().getParent().getEmail());
            reservation_going.add(reservationHack);
        }

        List<Reservation> reservation_returnX = reservationRepository.findReservationByLineName(line_name, "RETURN", date);
        List<ReservationHack> reservation_return = new ArrayList<>();

        for(Reservation reservation: reservation_returnX){
            ReservationHack reservationHack = new ReservationHack();
            reservationHack.setId(reservation.getId());
            reservationHack.setStatus(reservation.getStatus());
            reservationHack.setDate(reservation.getDate());
            reservationHack.setStopLine(reservation.getStopLine());
            reservationHack.setParent(reservation.getChild().getParent().getEmail());
            reservationHack.setChild(reservation.getChild().getChildName());
            reservation_return.add(reservationHack);
        }

        List<Child> child_going = childRepository.findDefaultReservation(line_name, "GOING", date);

        List<Child> child_return = childRepository.findDefaultReservation(line_name, "RETURN", date);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand().toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponseReservation(true, "Reservation succeeded!", reservation_going, reservation_return, child_going, child_return));
    }

    @PutMapping("reservations/{nome_linea}/{data}/{reservation_id}")
    public ResponseEntity<?> updateReservation(@PathVariable("nome_linea") String line_name, @PathVariable("data") String date,
                                               @PathVariable("reservation_id") Long id, @RequestBody TempForReservation tempForReservation, @CurrentUser UserPrincipal userPrincipal){
        Optional<Reservation> reservation1 = reservationRepository.findById(id);
        StopLine stopLine = stopLineRepository.findStopLineByLineNameAndDirectionAndStopId(line_name, tempForReservation.getDirection(), tempForReservation.getStop_id());
        Reservation reservation = reservation1.get();
        reservation.setId(id);
        reservation.setStatus(tempForReservation.getStatus());
        reservation.setChild(childRepository.findChildByChildName(tempForReservation.getName()));
        reservation.setStopLine(stopLine);
        reservationRepository.save(reservation);

        if(reservation.getStatus() != null){
            Message message = new Message();
            message.setToUser(reservation.getChild().getParent().getEmail());
            message.setFromUser(userPrincipal.getEmail());
            message.setMess(userPrincipal.getEmail() + " has taken your child " + reservation.getChild().getChildName() + " :)");
            message.setRead(false);
            message.setUser(reservation.getChild().getParent());
            messageRepository.save(message);

            if(!message.getToUser().equals(message.getFromUser())){
                try {
                    webSocketController.sendSpecific(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand(reservation).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponseUser(true, "Reservation succeeded!"));
    }

    //Useless
    private Reservation deleteReservation(Long id){
        Reservation reservation = reservationRepository.findById(id).get();
        reservation.setStatus("deleted");
        reservationRepository.save(reservation);

        return reservation;
    }

    @GetMapping("reservations/{nome_linea}/{data}/{reservation_id}")
    public Optional<Reservation> showReservation(@PathVariable("nome_linea") String line_name, @PathVariable("data") String date, @PathVariable("reservation_id") Long id){
        return reservationRepository.findById(id);
    }
}
