package com.sgu.orderservice.model;


import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("order_detail")
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
public class OrderDetail {
    @Id
    @Field("posts_id")
    private ObjectId postsId;
    @Field("order_id")
    private ObjectId orderId;
    @Field("title")
    private String title;
    @Field("description")
    private String description;
    @Field("price")
    private Long price;
    @Field("thumbnail")
    private String thumbnail;
    @Field("category_slug")
    private String categorySlug;
}
