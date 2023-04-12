package com.sgu.userservice.repository;

import com.sgu.userservice.model.Person;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface PersonRepository extends MongoRepository<Person, ObjectId> {

    public Optional<Object> getByPhone(String phone);
}
