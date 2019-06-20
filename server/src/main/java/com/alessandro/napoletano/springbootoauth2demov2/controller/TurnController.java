package com.alessandro.napoletano.springbootoauth2demov2.controller;

import com.alessandro.napoletano.springbootoauth2demov2.model.Line;
import com.alessandro.napoletano.springbootoauth2demov2.model.Message;
import com.alessandro.napoletano.springbootoauth2demov2.model.User;
import com.alessandro.napoletano.springbootoauth2demov2.model.stopline.StopLine;
import com.alessandro.napoletano.springbootoauth2demov2.model.turn.TempForTurn;
import com.alessandro.napoletano.springbootoauth2demov2.model.turn.Turn;
import com.alessandro.napoletano.springbootoauth2demov2.model.turn.TurnHack;
import com.alessandro.napoletano.springbootoauth2demov2.payload.ApiResponse;
import com.alessandro.napoletano.springbootoauth2demov2.payload.ApiResponseTurn;
import com.alessandro.napoletano.springbootoauth2demov2.payload.ApiResponseTurns;
import com.alessandro.napoletano.springbootoauth2demov2.payload.ApiResponseUser;
import com.alessandro.napoletano.springbootoauth2demov2.repository.*;
import com.alessandro.napoletano.springbootoauth2demov2.security.CurrentUser;
import com.alessandro.napoletano.springbootoauth2demov2.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
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

    @PostMapping("turns")
    public ResponseEntity<?> postTurn(@RequestBody TempForTurn tempForTurn) throws ChangeSetPersister.NotFoundException {
        Line line = lineRepository.findById(tempForTurn.getIdLine()).orElseThrow(ChangeSetPersister.NotFoundException::new);
        StopLine stopLineStart = stopLineRepository.findById(tempForTurn.getStopLineStart()).orElseThrow(ChangeSetPersister.NotFoundException::new);
        StopLine stopLineArrival = stopLineRepository.findById(tempForTurn.getStopLineArrival()).orElseThrow(ChangeSetPersister.NotFoundException::new);
        User user = userRepository.findByEmail(tempForTurn.getPersonName()).orElseThrow(ChangeSetPersister.NotFoundException::new);

        Turn turn = new Turn();
        turn.setDirection(tempForTurn.getDirection());
        turn.setDate(tempForTurn.getDate());
        turn.setLine(line);
        turn.setUser(user);
        turn.setStopLine_start(stopLineStart);
        turn.setStopLine_arrival(stopLineArrival);
        turnRepository.save(turn);
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand(turn).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponseTurn(true, "Turn added!", turn.createTurnHack()));
    }

    @PutMapping("turns/{turn_id}")
    public ResponseEntity<?> updateTurn(@PathVariable("turn_id") Long id, @RequestBody TempForTurn tempForTurn, @CurrentUser UserPrincipal userPrincipal) throws ChangeSetPersister.NotFoundException {
        Turn turn = turnRepository.findById(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        Line line = lineRepository.findById(tempForTurn.getIdLine()).orElseThrow(ChangeSetPersister.NotFoundException::new);
        StopLine stopLineStart = stopLineRepository.findById(tempForTurn.getStopLineStart()).orElseThrow(ChangeSetPersister.NotFoundException::new);
        StopLine stopLineArrival = stopLineRepository.findById(tempForTurn.getStopLineArrival()).orElseThrow(ChangeSetPersister.NotFoundException::new);

        turn.setStatus(tempForTurn.getStatus());
        turn.setDirection(tempForTurn.getDirection());
        turn.setLine(line);
        turn.setStopLine_start(stopLineStart);
        turn.setStopLine_arrival(stopLineArrival);
        turn.setUser(userRepository.findByEmail(tempForTurn.getPersonName()).get());
        turnRepository.save(turn);

        if(turn.getStatus().equals("CONFIRMED") &&!turn.getUser().getEmail().equals(userPrincipal.getEmail())) {
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
        }else if(turn.getStatus() == null &&!turn.getUser().getEmail().equals(userPrincipal.getEmail())) {
            Message message = new Message();
            message.setFromUser(userPrincipal.getEmail());
            message.setRead(false);
            message.setToUser(turn.getUser().getEmail());
            message.setMess(userPrincipal.getEmail() + " has deleted one of your turns");
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

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand(turn).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponseTurn(true, "Turn updated!", turn.createTurnHack()));
    }

    @DeleteMapping("turns/{turn_id}")
    public ResponseEntity<?> deleteTurn(@PathVariable("turn_id") Long id){
        turnRepository.deleteById(id);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand().toUri();
        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "Turn deleted!"));
    }

    @GetMapping("turns")
    public ResponseEntity<?> getTurns(@RequestParam(value = "line_id", required = false) Integer id){
        List<Turn> foundTurns;
        if(id != null){
            foundTurns = turnRepository.findAllByLine_Id(id);
        }else{
            foundTurns = turnRepository.findAll();
        }

        List<TurnHack> turns = foundTurns.stream().map(turn -> {
            return turn.createTurnHack();
        }).collect(Collectors.toList());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand(turns).toUri();
        return ResponseEntity.created(location)
                .body(new ApiResponseTurns(true, "Here are the turns!", turns));
    }
}


