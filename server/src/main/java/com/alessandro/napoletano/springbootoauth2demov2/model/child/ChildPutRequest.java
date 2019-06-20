package com.alessandro.napoletano.springbootoauth2demov2.model.child;

import com.alessandro.napoletano.springbootoauth2demov2.model.Line;
import com.alessandro.napoletano.springbootoauth2demov2.model.User;
import com.alessandro.napoletano.springbootoauth2demov2.model.stopline.StopLine;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Data
@AllArgsConstructor
public class ChildPutRequest {
    private String childName;
    private User parent;
    private Line lineDefault;
    private StopLine defaultGoing;
    private StopLine defaultReturn;
}
