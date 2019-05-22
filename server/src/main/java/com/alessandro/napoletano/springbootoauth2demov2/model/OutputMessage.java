package com.alessandro.napoletano.springbootoauth2demov2.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OutputMessage {
    private Message message;
    private String time;
}
