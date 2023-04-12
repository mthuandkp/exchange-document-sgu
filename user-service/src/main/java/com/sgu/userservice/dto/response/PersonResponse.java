package com.sgu.userservice.dto.response;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
public class PersonResponse {
    private ObjectId id;
    private String name;
    private String address;
    private String phone;
    private String birthday;
    private Boolean gender;
    private String createdAt;
    private String updatedAt;

    public String getId() {
        return id.toString();
    }
}
