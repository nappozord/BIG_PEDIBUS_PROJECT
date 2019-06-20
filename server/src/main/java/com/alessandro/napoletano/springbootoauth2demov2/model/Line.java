package com.alessandro.napoletano.springbootoauth2demov2.model;

import com.alessandro.napoletano.springbootoauth2demov2.model.stopline.StopLine;
import com.alessandro.napoletano.springbootoauth2demov2.model.turn.Turn;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Data
@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "lines", cascade = CascadeType.ALL)
    private List<User> users = new ArrayList<>();

    private String email;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "direction = 'GOING'")
    private List<StopLine> stopLines_going = new ArrayList<>();

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "direction = 'RETURN'")
    private List<StopLine> stopLines_return = new ArrayList<>();

    @OneToMany(mappedBy = "line", orphanRemoval = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Turn> turns;

    public Line() {}

    public Line(String name) {
        this.name = name;
    }

    public Line(String name, List<User> users) {
        this.name = name;
        this.users = users;
    }

    public Line(String name, List<StopLine> stopLines_going, List<StopLine> stopLines_return) {
        this.name = name;
        this.stopLines_going = stopLines_going;
        this.stopLines_return = stopLines_return;
    }
}
