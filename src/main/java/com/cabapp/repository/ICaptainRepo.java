package com.cabapp.repository;

import com.cabapp.model.Captain;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ICaptainRepo extends MongoRepository<Captain,String> {
    Optional<Captain> findByEmail(String email);
}
