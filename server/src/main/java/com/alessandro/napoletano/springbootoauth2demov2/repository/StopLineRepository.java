package com.alessandro.napoletano.springbootoauth2demov2.repository;

import com.alessandro.napoletano.springbootoauth2demov2.model.stopline.StopLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StopLineRepository extends JpaRepository<StopLine, Long> {
    @Query(value = "SELECT *\n" +
            "FROM public.stopline AS s, public.line AS l\n" +
            "WHERE l.name = :line\n" +
            "AND l.id = s.line_id\n" +
            "AND s.direction = :dir ;", nativeQuery = true)
    List<StopLine> findAllStopLinesByLineName(@Param("line") String line_name, @Param("dir") String direction);

    @Query(value = "SELECT * FROM public.stopline AS s, public.line AS l\n" +
            "WHERE l.name = :line\n" +
            "AND l.id = s.line_id\n" +
            "AND s.direction = :dir\n" +
            "AND s.stop_id = :stop_id ;", nativeQuery = true)
    StopLine findStopLineByLineNameAndDirectionAndStopId(@Param("line") String line_name, @Param("dir") String direction, @Param("stop_id") Long Stop_id);
}
