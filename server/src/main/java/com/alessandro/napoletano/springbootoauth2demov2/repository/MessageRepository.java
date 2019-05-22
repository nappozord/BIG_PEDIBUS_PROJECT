package com.alessandro.napoletano.springbootoauth2demov2.repository;

import com.alessandro.napoletano.springbootoauth2demov2.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByUser_Id(Long id);
}
