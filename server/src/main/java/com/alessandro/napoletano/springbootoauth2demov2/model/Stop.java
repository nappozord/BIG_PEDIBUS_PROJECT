package com.alessandro.napoletano.springbootoauth2demov2.model;

import com.alessandro.napoletano.springbootoauth2demov2.model.stopline.StopLine;
import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
@Data
@Entity
public class Stop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @OneToMany(mappedBy = "stop", orphanRemoval = true)
    @JsonIgnore
    private List<StopLine> stopLines;

    public Stop() {}

    public Stop(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stop stop = (Stop) o;
        return Objects.equals(this.name, stop.name);
    }
}
