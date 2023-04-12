package com.sgu.postsservice.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Category {
    @Field("id")
    private String id;
    @Field("name")
    private String name;
    @Field("url")
    private String url;
    @Field("category_slug")
    private String categorySlug;
}
