package com.alessandro.napoletano.springbootoauth2demov2.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Data
public class ImportedStop {
    private String name;

    public ImportedStop() {}

    public ImportedStop(String name) {
        this.name = name;
    }
}
