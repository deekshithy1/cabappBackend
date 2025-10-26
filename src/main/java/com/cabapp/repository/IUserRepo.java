package com.cabapp.repository;

import com.cabapp.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface IUserRepo extends MongoRepository<User,String> {

    Optional<User>findByEmail(String email);
}
