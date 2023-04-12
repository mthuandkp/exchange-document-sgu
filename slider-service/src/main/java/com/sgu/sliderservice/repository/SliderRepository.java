package com.sgu.sliderservice.repository;

import com.sgu.sliderservice.model.Slider;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SliderRepository extends MongoRepository<Slider, ObjectId> {
}
