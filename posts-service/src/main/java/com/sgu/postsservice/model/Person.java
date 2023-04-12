package com.sgu.postsservice.model;

import lombok.Builder;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

public class Person {
    private ObjectId id;
    private String name;
    private String address;
    private String phone;
    private String birthday;
    private Boolean gender;
    private String createdAt;
    private String updatedAt;
}
