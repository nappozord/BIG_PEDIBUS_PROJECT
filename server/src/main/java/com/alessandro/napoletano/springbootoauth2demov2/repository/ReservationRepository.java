package com.alessandro.napoletano.springbootoauth2demov2.repository;

import com.alessandro.napoletano.springbootoauth2demov2.model.child.Child;
import com.alessandro.napoletano.springbootoauth2demov2.model.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query(value = "SELECT * " +
            "FROM public.reservation AS r, public.stopline AS s, public.line AS l " +
            "WHERE l.name = :line " +
            "AND l.id = s.line_id " +
            "AND r.stopline_id = s.id " +
            "AND r.date = :date_param " +
            "AND s.direction = :dir ;", nativeQuery = true)
    List<Reservation> findReservationByLineName(@Param("line") String line_name, @Param("dir") String direction, @Param("date_param") String date);
}
