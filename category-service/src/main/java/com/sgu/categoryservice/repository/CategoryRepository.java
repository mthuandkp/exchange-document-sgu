package com.sgu.categoryservice.repository;

import com.sgu.categoryservice.model.Category;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CategoryRepository extends MongoRepository<Category, ObjectId> {
    public Optional<Category> findByName(String name);


    public Optional<Category> findByCategorySlug(String categorySlug);
}
