package com.alessandro.napoletano.springbootoauth2demov2.model;

import com.alessandro.napoletano.springbootoauth2demov2.model.stopline.StopLine;
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

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "direction = 'GOING'")
    private List<StopLine> stopLines_going = new ArrayList<>();

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "direction = 'RETURN'")
    private List<StopLine> stopLines_return = new ArrayList<>();

    @ManyToOne
    private User admin;

    public Line() {}

    public Line(String name) {
        this.name = name;
    }

    public Line(String name, List<StopLine> stopLines_going, List<StopLine> stopLines_return) {
        this.name = name;
        this.stopLines_going = stopLines_going;
        this.stopLines_return = stopLines_return;
    }
}
