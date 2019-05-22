package com.alessandro.napoletano.springbootoauth2demov2.controller;

import com.alessandro.napoletano.springbootoauth2demov2.model.Line;
import com.alessandro.napoletano.springbootoauth2demov2.model.Message;
import com.alessandro.napoletano.springbootoauth2demov2.model.turn.TempForTurn;
import com.alessandro.napoletano.springbootoauth2demov2.model.turn.Turn;
import com.alessandro.napoletano.springbootoauth2demov2.model.turn.id_list;
import com.alessandro.napoletano.springbootoauth2demov2.payload.ApiResponseReservation;
import com.alessandro.napoletano.springbootoauth2demov2.payload.ApiResponseTurn;
import com.alessandro.napoletano.springbootoauth2demov2.payload.ApiResponseUser;
import com.alessandro.napoletano.springbootoauth2demov2.repository.*;
import com.alessandro.napoletano.springbootoauth2demov2.security.CurrentUser;
import com.alessandro.napoletano.springbootoauth2demov2.security.UserPrincipal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class TurnController {

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private StopLineRepository stopLineRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private WebSocketController webSocketController;
    
    @Autowired
    private TurnRepository turnRepository;

    @PostMapping("turns/{nome_linea}/{data}")
    public ResponseEntity<?> postTurn(@PathVariable("nome_linea") String line_name, @PathVariable("data") String date, @RequestBody TempForTurn tempForTurn){
        Line line = lineRepository.findByName(line_name);
        Turn turn = new Turn();
        turn.setDirection(tempForTurn.getDirection());
        turn.setDate(date);
        turn.setLine(line);
        turn.setUser(userRepository.findByEmail(tempForTurn.getName()).get());
        turn.setStopLine_start(tempForTurn.getStopLine_start());
        turn.setStopLine_arrival(tempForTurn.getStopLine_arrival());
        turnRepository.save(turn);
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand(turn).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponseReservation(true, "Turn added!", turn.getId()));
    }

    @PutMapping("turns/{nome_linea}/{data}/{turn_id}")
    public ResponseEntity<?> updateTurn(@PathVariable("nome_linea") String line_name, @PathVariable("data") String date, @PathVariable("turn_id") Long id, @RequestBody TempForTurn tempForTurn){
        Optional<Turn> turn1 = turnRepository.findById(id);
        Line line = lineRepository.findByName(line_name);
        Turn turn = turn1.get();
        turn.setId(id);
        turn.setStatus(null);
        turn.setDirection(tempForTurn.getDirection());
        turn.setLine(line);
        turn.setStopLine_start(tempForTurn.getStopLine_start());
        turn.setStopLine_arrival(tempForTurn.getStopLine_arrival());
        turn.setUser(userRepository.findByEmail(tempForTurn.getName()).get());
        turnRepository.save(turn);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand(turn).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponseUser(true, "Turn updated!"));
    }

    @DeleteMapping("turns/{turn_id}")
    private ResponseEntity<?> deleteTurn(@PathVariable("turn_id") Long id){
        turnRepository.deleteById(id);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand().toUri();
        return ResponseEntity.created(location)
                .body(new ApiResponseUser(true, "Turn deleted!"));
    }

    @GetMapping("turns/{line_id}")
    private ResponseEntity<?> getTurnsByLine(@PathVariable("line_id") Integer id){
        List<Turn> turns = turnRepository.findAllByLine_Id(id);
        List<String> users = new ArrayList<>();

        for (int i = 0; i < turns.size(); i++) {
            users.add(turns.get(i).getUser().getEmail());
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand(turns).toUri();
        return ResponseEntity.created(location)
                .body(new ApiResponseTurn(true, "Here are the turns!", turns, users));
    }

    @PutMapping("turns/confirmTurn")
    private ResponseEntity<?> confirmTurn(@RequestBody id_list id_list, @CurrentUser UserPrincipal userPrincipal) {

        for(Long id: id_list.getId_conf()){
            Turn turn = turnRepository.findById(id).get();
            turn.setStatus("CONFIRMED");
            turnRepository.save(turn);

            if(!turn.getUser().getEmail().equals(userPrincipal.getEmail())) {
                Message message = new Message();
                message.setFromUser(userPrincipal.getEmail());
                message.setRead(false);
                message.setToUser(turn.getUser().getEmail());
                message.setMess(userPrincipal.getEmail() + " has confirmed one of your turns");
                message.setUser(turn.getUser());
                messageRepository.save(message);

                if (!message.getToUser().equals(message.getFromUser())) {
                    try {
                        webSocketController.sendSpecific(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        for(Long id: id_list.getId_canc()){
            Turn turn = turnRepository.findById(id).get();
            turn.setStatus(null);
            turnRepository.save(turn);

            if(!turn.getUser().getEmail().equals(userPrincipal.getEmail())){
                Message message = new Message();
                message.setUser(turn.getUser());
                message.setRead(false);
                message.setToUser(turn.getUser().getEmail());
                message.setFromUser(userPrincipal.getEmail());
                message.setMess(userPrincipal.getEmail() + " has cancelled one of your turns");
                messageRepository.save(message);

                if(!message.getToUser().equals(message.getFromUser())){
                    try {
                        webSocketController.sendSpecific(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand().toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponseUser(true, "Turns confirmed!"));
    }
}


