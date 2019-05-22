package com.alessandro.napoletano.springbootoauth2demov2.repository;

import com.alessandro.napoletano.springbootoauth2demov2.model.Line;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineRepository extends JpaRepository<Line, Long> {
    Line findByName(String line);
}
