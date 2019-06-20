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

@Data
public class ImportedStopLine {
    private String hour;
    private ImportedStop stop;

    public ImportedStopLine() {}

    public ImportedStopLine(String hour) {
        this.stop = stop;
    }

    public ImportedStopLine(String hour, ImportedStop stop) {
        this.hour = hour;
        this.stop = stop;
    }
}
