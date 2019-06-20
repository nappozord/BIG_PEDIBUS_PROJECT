package com.alessandro.napoletano.springbootoauth2demov2.service;

import com.alessandro.napoletano.springbootoauth2demov2.model.Line;
import com.alessandro.napoletano.springbootoauth2demov2.model.Stop;
import com.alessandro.napoletano.springbootoauth2demov2.model.User;
import com.alessandro.napoletano.springbootoauth2demov2.model.stopline.StopLine;
import com.alessandro.napoletano.springbootoauth2demov2.repository.LineRepository;
import com.alessandro.napoletano.springbootoauth2demov2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class IntroService {

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private UserRepository userRepository;

    public void save(List<Line> lines){

        List<Stop> stops = new ArrayList<>();
        List<Line> lines_to_save = new ArrayList<>();

        for(Line line : lines){
            Line line1 = new Line(line.getName());
            for(StopLine stopLine : line.getStopLines_going()){
                Stop stop1 = stopLine.getStop();
                int flag = 0;
                for(Stop stop : stops){
                    if(stop.equals(stopLine.getStop())){
                        stop1 = stop;
                        flag = 1;
                    }
                }
                if(flag == 0){
                    stops.add(stop1);
                }
                StopLine stopLine1 = new StopLine();
                stopLine1.setDirection(StopLine.Direction.GOING);
                stopLine1.setHour(stopLine.getHour());
                stopLine1.setLine(line1);
                stopLine1.setStop(stop1);
                line1.getStopLines_going().add(stopLine1);
            }
            for(StopLine stopLine : line.getStopLines_return()){
                Stop stop1 = stopLine.getStop();
                int flag = 0;
                for(Stop stop : stops){
                    if(stop.equals(stopLine.getStop())){
                        stop1 = stop;
                        flag = 1;
                    }
                }
                if(flag == 0){
                    stops.add(stop1);
                }
                StopLine stopLine1 = new StopLine();
                stopLine1.setDirection(StopLine.Direction.RETURN);
                stopLine1.setHour(stopLine.getHour());
                stopLine1.setLine(line1);
                stopLine1.setStop(stop1);
                line1.getStopLines_return().add(stopLine1);
            }
            lines_to_save.add(line1);
        }
        lineRepository.saveAll(lines_to_save);
    }

    public void setLineAdmin(String lineName, String adminEmail){
        Line line = lineRepository.findByName(lineName);
        Optional<User> u = userRepository.findByEmail(adminEmail);

        if(line != null && u.isPresent()){
            User user = u.get();
            user.getLines().add(line);
        }

    }

}
