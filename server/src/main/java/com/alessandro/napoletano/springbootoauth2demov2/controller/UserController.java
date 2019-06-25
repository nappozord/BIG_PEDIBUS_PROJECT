package com.alessandro.napoletano.springbootoauth2demov2.controller;

import com.alessandro.napoletano.springbootoauth2demov2.exception.ResourceNotFoundException;
import com.alessandro.napoletano.springbootoauth2demov2.model.Message;
import com.alessandro.napoletano.springbootoauth2demov2.model.turn.TurnHack;
import com.alessandro.napoletano.springbootoauth2demov2.repository.MessageRepository;
import com.alessandro.napoletano.springbootoauth2demov2.service.ChildService;
import com.alessandro.napoletano.springbootoauth2demov2.model.child.Child;
import com.alessandro.napoletano.springbootoauth2demov2.model.Line;
import com.alessandro.napoletano.springbootoauth2demov2.model.User;
import com.alessandro.napoletano.springbootoauth2demov2.payload.ApiResponseUser;
import com.alessandro.napoletano.springbootoauth2demov2.payload.ChildTmp;
import com.alessandro.napoletano.springbootoauth2demov2.payload.UpdateRequest;
import com.alessandro.napoletano.springbootoauth2demov2.repository.ChildRepository;
import com.alessandro.napoletano.springbootoauth2demov2.repository.LineRepository;
import com.alessandro.napoletano.springbootoauth2demov2.repository.UserRepository;
import com.alessandro.napoletano.springbootoauth2demov2.security.CurrentUser;
import com.alessandro.napoletano.springbootoauth2demov2.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private ChildService childService;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private WebSocketController webSocketController;

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId()).get();

        List<TurnHack> turnHacks = new ArrayList<>();

        for(int i = 0; i < user.getTurns().size(); i++){
            TurnHack turnHack = new TurnHack();
            turnHack.setId(user.getTurns().get(i).getId());
            turnHack.setStatus(user.getTurns().get(i).getStatus());
            turnHack.setDirection(user.getTurns().get(i).getDirection());
            turnHack.setDate(user.getTurns().get(i).getDate());
            turnHack.setLine(user.getTurns().get(i).getLine());
            turnHack.setStopLine_start(user.getTurns().get(i).getStopLine_start());
            turnHack.setStopLine_arrival(user.getTurns().get(i).getStopLine_arrival());
            turnHacks.add(turnHack);
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand(user).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponseUser(true, "Here are your children!", user, turnHacks));
    }

    @GetMapping("/user/{userEmail}")
    @PreAuthorize("hasRole('USER')")
    public User getUser(@PathVariable String userEmail) {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
    }

    @PutMapping("/user/{userEmail}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateLinesAndUsers(@PathVariable String userEmail, @RequestBody UpdateRequest updateRequest, @CurrentUser UserPrincipal userPrincipal) throws Exception {
        Optional<User> userX = userRepository.findByEmail(userEmail);
        User user = userX.get();

        user.getLines().clear();

        List<Line> lines = updateRequest.getLines();

        for(int i=0; i < lines.size(); i++){
            user.getLines().add(lineRepository.findByName(lines.get(i).getName()));
        }

        List<ChildTmp> children = updateRequest.getChildren();

        for(int i=0; i < children.size(); i++){
            ChildTmp childTMP = children.get(i);
            if(childTMP.getStatus() != null) {
                if (!childTMP.getStatus().equals("")) {
                    if (childTMP.getStatus().equals("updated")) {
                        if (childTMP.getOld_name() == null) {
                            Child child = new Child();
                            child.setChildName(childTMP.getChildName());
                            child.setParent(user);
                            user.getChildren().add(child);
                        } else {
                            Child child = childRepository.findChildByChildName(childTMP.getOld_name());
                            child.setChildName(childTMP.getChildName());
                            childRepository.save(child);
                        }
                    } else {
                        if (childTMP.getOld_name() != null) {
                            Child child = new Child();
                            child.setChildName(childTMP.getOld_name());
                            user.getChildren().remove(child);
                            childService.removeChildByName(childTMP.getChildName());
                        }
                    }
                }
            }
        }

        if(!user.getRole().equals("SYS_ADMIN")){
            if(updateRequest.getLines().size() == 0){
                user.setRole("USER");
            } else {
                user.setRole("ADMIN");
            }
        }

        User result = userRepository.save(user);

        if(!userPrincipal.getEmail().equals(result.getEmail())){
            Message message = new Message();
            message.setFromUser(userPrincipal.getEmail());
            message.setToUser(result.getEmail());
            message.setMess(userPrincipal.getEmail() + " has updated your ADMIN permissions");
            message.setRead(false);
            message.setUser(result);
            messageRepository.save(message);

            if(!message.getToUser().equals(message.getFromUser())){
                webSocketController.sendSpecific(message);
            }
        }


        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand(result.getId()).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponseUser(true, "Information updated@"));
    }
}
