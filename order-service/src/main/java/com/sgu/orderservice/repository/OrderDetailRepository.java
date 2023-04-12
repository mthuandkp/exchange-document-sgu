package com.sgu.orderservice.repository;

import com.sgu.orderservice.model.OrderDetail;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface OrderDetailRepository extends MongoRepository<OrderDetail, ObjectId> {
    @Override
    Optional<OrderDetail> findById(ObjectId objectId);

    Optional<OrderDetail> findByOrderId(ObjectId id);

}
