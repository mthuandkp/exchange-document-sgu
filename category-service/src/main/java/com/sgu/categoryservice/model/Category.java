package com.sgu.categoryservice.model;

import com.sgu.categoryservice.utils.DateUtils;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document("category")
@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
public class Category {
    @Id
    @Field("id")
    private ObjectId id;
    @Field("name")
    private String name;
    @Field("url")
    private String url;
    @Field("created_at")
    @Builder.Default
    private String createdAt = DateUtils.getNow();
    @Field("updated_at")
    @Builder.Default
    private String updatedAt = DateUtils.getNow();
    @Field("category_slug")
    private String categorySlug;
}
