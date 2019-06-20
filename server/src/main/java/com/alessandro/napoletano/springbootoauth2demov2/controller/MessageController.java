package com.alessandro.napoletano.springbootoauth2demov2.controller;

import com.alessandro.napoletano.springbootoauth2demov2.model.Message;
import com.alessandro.napoletano.springbootoauth2demov2.payload.ApiResponse;
import com.alessandro.napoletano.springbootoauth2demov2.payload.ApiResponseUser;
import com.alessandro.napoletano.springbootoauth2demov2.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Controller
public class MessageController {

    @Autowired
    MessageRepository messageRepository;

    @GetMapping("/messages/{id}")
    public ResponseEntity<?> getMessages(@PathVariable("id") Long id){
        List<Message> messages = this.messageRepository.findAllByUser_Id(id);

        for(int i = 0; i < messages.size(); i++){
            messages.get(i).setRead(true);
        }

        this.messageRepository.saveAll(messages);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand().toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "OK!"));
    }

}
