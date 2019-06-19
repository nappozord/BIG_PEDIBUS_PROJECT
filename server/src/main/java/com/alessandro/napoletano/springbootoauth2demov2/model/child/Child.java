package com.alessandro.napoletano.springbootoauth2demov2.model.child;

import com.alessandro.napoletano.springbootoauth2demov2.model.Line;
import com.alessandro.napoletano.springbootoauth2demov2.model.User;
import com.alessandro.napoletano.springbootoauth2demov2.model.reservation.Reservation;
import com.alessandro.napoletano.springbootoauth2demov2.model.stopline.StopLine;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "child")
public class Child {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String childName;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    private User parent;

    @ManyToOne
    private Line lineDefault;

    @ManyToOne
    private StopLine defaultGoing;

    @ManyToOne
    private StopLine defaultReturn;

    @OneToMany(mappedBy = "child", orphanRemoval = true)
    private List<Reservation> reservations;

    @Override
    public boolean equals(Object o){
        Child child = (Child) o;
        if(child.getChildName().equals(this.childName)){
            return true;
        }
        return false;
    }
}
