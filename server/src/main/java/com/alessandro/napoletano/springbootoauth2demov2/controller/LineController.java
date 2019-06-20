package com.alessandro.napoletano.springbootoauth2demov2.controller;

import com.alessandro.napoletano.springbootoauth2demov2.model.Line;
import com.alessandro.napoletano.springbootoauth2demov2.payload.ApiResponseLine;
import com.alessandro.napoletano.springbootoauth2demov2.payload.ApiResponseLines;
import com.alessandro.napoletano.springbootoauth2demov2.payload.ApiResponseLinesNames;
import com.alessandro.napoletano.springbootoauth2demov2.repository.LineRepository;
import com.alessandro.napoletano.springbootoauth2demov2.repository.StopLineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
public class LineController {

    @Autowired
    private LineRepository lineRepository;

    @GetMapping("lines")
    public ResponseEntity<?>getAll( @RequestParam(value = "only_the_names", required = false) Boolean onlyTheNames){

        List<Line> lines = lineRepository.findAll();

        if(onlyTheNames != null && onlyTheNames.booleanValue()){
            List<String> linesNameList = new ArrayList<>();
            for(Line line : lines){
                linesNameList.add(line.getName());
            }
            URI location = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .buildAndExpand(lines).toUri();

            return ResponseEntity.created(location)
                    .body(new ApiResponseLinesNames(true, "The names of all the lines in the system", linesNameList));
        }else{
            URI location = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .buildAndExpand(lines).toUri();

            return ResponseEntity.created(location)
                    .body(new ApiResponseLines(true, "All the lines in the system", lines));
        }
    }

    @GetMapping("lines/{nome_linea}")
    public ResponseEntity<?> getLine(@PathVariable("nome_linea") String line_name){

        Line line = lineRepository.findByName(line_name);


        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .buildAndExpand(line).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponseLine(true, "Your request line", line));
    }
}
