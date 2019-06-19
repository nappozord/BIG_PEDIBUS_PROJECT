package com.alessandro.napoletano.springbootoauth2demov2.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class ImportedLine {
    private String name;
    private String email;

    private List<ImportedStopLine> stopLines_going = new ArrayList<>();
    private List<ImportedStopLine> stopLines_return = new ArrayList<>();

    public ImportedLine() {}

    public ImportedLine(String name) {
        this.name = name;
    }

    public ImportedLine(String name, List<ImportedStopLine> stopLines_going, List<ImportedStopLine> stopLines_return) {
        this.name = name;
        this.stopLines_going = stopLines_going;
        this.stopLines_return = stopLines_return;
    }
}
