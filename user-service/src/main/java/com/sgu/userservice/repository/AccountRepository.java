package com.sgu.userservice.repository;

import com.sgu.userservice.model.Account;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface AccountRepository extends MongoRepository<Account, ObjectId> {

    @Query("{username:?0}")
    public Optional<Account> findByUsername(String username);

    @Query("{role:'USER'}")
    Page<Account> findAllUserAccount(Pageable pageable);
}
