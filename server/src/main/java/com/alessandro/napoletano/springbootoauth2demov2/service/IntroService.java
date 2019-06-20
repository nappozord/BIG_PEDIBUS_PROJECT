package com.alessandro.napoletano.springbootoauth2demov2.service;

import com.alessandro.napoletano.springbootoauth2demov2.model.*;
import com.alessandro.napoletano.springbootoauth2demov2.model.child.Child;
import com.alessandro.napoletano.springbootoauth2demov2.model.reservation.Reservation;
import com.alessandro.napoletano.springbootoauth2demov2.model.reservation.TempForReservation;
import com.alessandro.napoletano.springbootoauth2demov2.model.stopline.StopLine;
import com.alessandro.napoletano.springbootoauth2demov2.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class IntroService {

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private StopRepository stopRepository;

    @Autowired
    private UserRepository userRepository;

    public void save(ImportedLine importedLine){

        List<Stop> stops = stopRepository.findAll();

        Line line = new Line(importedLine.getName());

        User admin = this.getUserByEmail(importedLine.getEmail());
        if(admin == null){
            return;
        }

        line.setAdmin(admin);

        for(ImportedStopLine importedStopLine : importedLine.getStopLines_going()){
            Stop importedStop = new Stop(importedStopLine.getStop().getName());

            int flag = 0;
            Stop foundStop = stops.stream().filter(stop -> {
                return stop.equals(importedStop);
            }).findFirst().orElse(null);

            if(foundStop == null){
                stopRepository.save(importedStop);
                stops.add(importedStop);
                foundStop = importedStop;
            }

            StopLine stopLine = new StopLine();
            stopLine.setDirection(StopLine.Direction.GOING);
            stopLine.setHour(importedStopLine.getHour());
            stopLine.setLine(line);
            stopLine.setStop(foundStop);
            line.getStopLines_going().add(stopLine);
        }

        for(ImportedStopLine importedStopLine : importedLine.getStopLines_return()){
            Stop importedStop = new Stop(importedStopLine.getStop().getName());

            int flag = 0;
            Stop foundStop = stops.stream().filter(stop -> {
                return stop.equals(importedStop);
            }).findFirst().orElse(null);

            if(foundStop == null){
                stopRepository.save(importedStop);
                stops.add(importedStop);
                foundStop = importedStop;
            }

            StopLine stopLine = new StopLine();
            stopLine.setDirection(StopLine.Direction.RETURN);
            stopLine.setHour(importedStopLine.getHour());
            stopLine.setLine(line);
            stopLine.setStop(foundStop);
            line.getStopLines_going().add(stopLine);
        }
        lineRepository.save(line);
    }

    public User getUserByEmail(String email){
       return userRepository.findByEmail(email).orElse(null);
    }

}
