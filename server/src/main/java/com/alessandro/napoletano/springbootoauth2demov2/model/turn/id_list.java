package com.alessandro.napoletano.springbootoauth2demov2.model.turn;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class id_list {
    List<Long> id_conf;
    List<Long> id_canc;
}
