package com.alessandro.napoletano.springbootoauth2demov2.controller;

import com.alessandro.napoletano.springbootoauth2demov2.model.Message;
import com.alessandro.napoletano.springbootoauth2demov2.model.OutputMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/stomp")
    public void sendSpecific(Message message) throws Exception {
        OutputMessage out = new OutputMessage(
                message, new SimpleDateFormat("HH:mm").format(new Date())
        );
        simpMessagingTemplate.convertAndSend("/topic/messages-" + message.getToUser(), out);
    }

}
