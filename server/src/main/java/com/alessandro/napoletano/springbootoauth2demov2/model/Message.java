package com.alessandro.napoletano.springbootoauth2demov2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Data
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mess_id;

    @ManyToOne
    @JoinColumn
    @JsonIgnore
    private User user;

    private String fromUser;

    private String toUser;

    private String mess;

    private Boolean read = false;

    public Message() {}
}
