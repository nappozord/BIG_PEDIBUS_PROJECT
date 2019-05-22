package com.alessandro.napoletano.springbootoauth2demov2.service;

import com.alessandro.napoletano.springbootoauth2demov2.repository.ChildRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class ChildService {

    @Autowired
    private ChildRepository childRepository;

    public void removeChildByName(String name){
        childRepository.removeChildByChildName(name);
    }

}
