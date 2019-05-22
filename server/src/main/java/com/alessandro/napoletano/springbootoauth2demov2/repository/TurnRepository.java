package com.alessandro.napoletano.springbootoauth2demov2.repository;

import com.alessandro.napoletano.springbootoauth2demov2.model.turn.Turn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TurnRepository extends JpaRepository<Turn, Long> {
    List<Turn> findAllByLine_Id(Integer id);
}
