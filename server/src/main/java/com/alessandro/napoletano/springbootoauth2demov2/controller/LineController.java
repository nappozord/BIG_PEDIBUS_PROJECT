package com.alessandro.napoletano.springbootoauth2demov2.controller;

import com.alessandro.napoletano.springbootoauth2demov2.model.Line;
import com.alessandro.napoletano.springbootoauth2demov2.payload.ApiResponseLines;
import com.alessandro.napoletano.springbootoauth2demov2.payload.ApiResponseStringList;
import com.alessandro.napoletano.springbootoauth2demov2.repository.LineRepository;
import com.alessandro.napoletano.springbootoauth2demov2.repository.StopLineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
public class LineController {

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private StopLineRepository stopLineRepository;

    @GetMapping("/lines")
    public ResponseEntity<?> getAll(){

        List<Line> lines = lineRepository.findAll();

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand(lines).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponseLines(true, "Here are the lines!", lines));
    }

    @GetMapping("/lines/{nome_linea}")
    public ResponseEntity<?> getStopLines(@PathVariable("nome_linea") String line_name){
        Line line = lineRepository.findByName(line_name);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand(line).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponseLines(true, "Here are the lines!", line, line.getUsers()));
    }

    @GetMapping("/lines/justTheNames")
    public ResponseEntity<?> getLinesNames(){

        List<Line> lines = lineRepository.findAll();

        List<String> lines_string = new ArrayList<>();
        for(Line line : lines){
            lines_string.add(line.getName());
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand(lines).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponseStringList(true, "Line Name List", lines_string));
    }

}
