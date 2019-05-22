package com.alessandro.napoletano.springbootoauth2demov2.repository;

import com.alessandro.napoletano.springbootoauth2demov2.model.child.Child;
import com.alessandro.napoletano.springbootoauth2demov2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChildRepository extends JpaRepository<Child, Long> {
    List<Child> findChildrenByParent(User user);

    Child findChildByChildName(String name);

    void removeChildByChildName(String name);

    @Query(value = "SELECT * FROM public.line AS l, public.child AS ch " +
            "WHERE l.name = :line AND ch.linedefault_id = l.id " +
            "AND ch.child_id NOT IN " +
            "(SELECT r.child_child_id FROM public.reservation AS r, public.stopline AS s, public.line AS l " +
            "WHERE l.id = s.line_id AND r.stopline_id = s.id " +
            "AND r.date = :date_param " +
            "AND s.direction = :dir) ;", nativeQuery = true)
    List<Child> findDefaultReservation(@Param("line") String line_name, @Param("dir") String direction, @Param("date_param") String date);
}
