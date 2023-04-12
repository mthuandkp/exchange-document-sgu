package com.sgu.userservice.model;

import com.sgu.userservice.utils.DateUtils;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("person")
@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
public class Person {
    @Id
    private ObjectId id;
    @Field("name")
    private String name;
    @Field("address")
    private String address;
    @Field("phone")
    private String phone;
    @Field("birthday")
    private String birthday;
    @Field("gender")
    private Boolean gender;
    @Field("created_at")
    @Builder.Default
    private String createdAt = DateUtils.getNow();
    @Field("updated_at")
    @Builder.Default
    private String updatedAt = DateUtils.getNow();
}
