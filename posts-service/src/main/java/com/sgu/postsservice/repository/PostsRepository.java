package com.sgu.postsservice.repository;

import com.sgu.postsservice.model.Posts;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PostsRepository extends MongoRepository<Posts, ObjectId> {
    Optional<Posts> getByPostsSlug(String slug);

    List<Posts> getByAccountId(ObjectId objectId);

    Optional<Posts> findByPostsSlug(String slug);
    //List<Posts> getByCategorySlug(String slug);
}
