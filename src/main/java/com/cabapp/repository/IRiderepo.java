package com.cabapp.repository;

import com.cabapp.dto.RideStartDTO;
import com.cabapp.model.Ride;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IRiderepo extends MongoRepository<Ride,String> {

    List<Ride> findAllByUserId(String userId);

    List<Ride> findAllByUser(String id);

}
