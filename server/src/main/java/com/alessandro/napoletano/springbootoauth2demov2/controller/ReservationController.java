package com.alessandro.napoletano.springbootoauth2demov2.controller;

import com.alessandro.napoletano.springbootoauth2demov2.model.Message;
import com.alessandro.napoletano.springbootoauth2demov2.model.reservation.Reservation;
import com.alessandro.napoletano.springbootoauth2demov2.model.reservation.ReservationHack;
import com.alessandro.napoletano.springbootoauth2demov2.model.reservation.TempForReservation;
import com.alessandro.napoletano.springbootoauth2demov2.model.stopline.StopLine;
import com.alessandro.napoletano.springbootoauth2demov2.payload.ApiResponse;
import com.alessandro.napoletano.springbootoauth2demov2.payload.ApiResponseReservation;
import com.alessandro.napoletano.springbootoauth2demov2.payload.ApiResponseReservations;
import com.alessandro.napoletano.springbootoauth2demov2.payload.ApiResponseUser;
import com.alessandro.napoletano.springbootoauth2demov2.repository.ChildRepository;
import com.alessandro.napoletano.springbootoauth2demov2.repository.MessageRepository;
import com.alessandro.napoletano.springbootoauth2demov2.repository.ReservationRepository;
import com.alessandro.napoletano.springbootoauth2demov2.repository.StopLineRepository;
import com.alessandro.napoletano.springbootoauth2demov2.security.CurrentUser;
import com.alessandro.napoletano.springbootoauth2demov2.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @PostMapping("reservations")
    public ResponseEntity<?> postReservation(@RequestBody TempForReservation tempForReservation, @CurrentUser UserPrincipal userPrincipal){
        StopLine stopLine = stopLineRepository.findById(tempForReservation.getStopLine_id()).orElse(null);
        if(stopLine != null){
            Reservation reservation = new Reservation();
            reservation.setStatus(tempForReservation.getStatus());
            reservation.setChild(childRepository.findChildByChildName(tempForReservation.getChild()));
            reservation.setDate(tempForReservation.getDate());
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
                    .body(new ApiResponseReservation(true, "Reservation succeeded!", reservation.createReservationHack()));
        }else{
            return ResponseEntity.status(404).build();
        }

    }

    @GetMapping("reservations/{nome_linea}/{data}")
    public ResponseEntity<?> getStopLinesWithReservations(@PathVariable("nome_linea") String line_name, @PathVariable("data") String date){
        List<Reservation> reservationsModel = reservationRepository.findReservationByLineName(line_name, date);

        List<ReservationHack> reservations = reservationsModel.stream().map(reservation -> {
            return reservation.createReservationHack();
        }).collect(Collectors.toList());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand().toUri();

        Date reservationsDate = new Date(new Long(date));
        return ResponseEntity.created(location)
                .body(new ApiResponseReservations(true, "The reservations of " + reservationsDate.toString(), reservations));
    }

    @PutMapping("reservations/{reservation_id}")
    public ResponseEntity<?> updateReservation(@PathVariable("reservation_id") Long id, @RequestBody TempForReservation tempForReservation,
                                               @CurrentUser UserPrincipal userPrincipal) throws ChangeSetPersister.NotFoundException{
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if(reservation != null) {
            StopLine stopLine = stopLineRepository.findById(tempForReservation.getStopLine_id()).orElse(null);

            if(stopLine != null) {
                reservation.setId(id);
                reservation.setStatus(tempForReservation.getStatus());
                reservation.setChild(childRepository.findChildByChildName(tempForReservation.getChild()));
                reservation.setStopLine(stopLine);
                reservationRepository.save(reservation);

                if (reservation.getStatus() != null) {
                    Message message = new Message();
                    message.setToUser(reservation.getChild().getParent().getEmail());
                    message.setFromUser(userPrincipal.getEmail());
                    message.setMess(userPrincipal.getEmail() + " has taken your child " + reservation.getChild().getChildName() + " :)");
                    message.setRead(false);
                    message.setUser(reservation.getChild().getParent());
                    messageRepository.save(message);

                    if (!message.getToUser().equals(message.getFromUser())) {
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
                        .body(new ApiResponseReservation(true, "Reservation updated!", reservation.createReservationHack()));
            }
        }
        throw new ChangeSetPersister.NotFoundException();
    }

    @GetMapping("reservations/{reservation_id}")
    public ResponseEntity<?> showReservation(@PathVariable("reservation_id") Long id) throws ChangeSetPersister.NotFoundException {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand(reservation).toUri();
        return ResponseEntity.created(location).body(new ApiResponseReservation(true, "Your reservation", reservation.createReservationHack()));
    }
}
